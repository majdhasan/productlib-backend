package com.meshhdawi.productlib.orders

import com.meshhdawi.productlib.cart.CartRepository
import com.meshhdawi.productlib.cart.CartService
import com.meshhdawi.productlib.messaging.email.EmailService
import com.meshhdawi.productlib.messaging.telegram.TelegramAdminRepository
import com.meshhdawi.productlib.messaging.telegram.TelegramBot
import com.meshhdawi.productlib.notifications.NotificationService
import com.meshhdawi.productlib.orders.orderitems.OrderItemEntity
import com.meshhdawi.productlib.orders.orderitems.OrderItemRepository
import com.meshhdawi.productlib.products.ProductService
import com.meshhdawi.productlib.users.UserRepository
import com.meshhdawi.productlib.users.UserRole
import com.meshhdawi.productlib.users.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val cartService: CartService,
    private val orderItemRepository: OrderItemRepository,
    private val emailService: EmailService,
    private val telegramBot: TelegramBot,
    private val telegramAdminRepository: TelegramAdminRepository,
    private val notificationService: NotificationService,
    private val productService: ProductService,
) {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    fun getAllOrders(): List<OrderEntity> {
        return orderRepository.findAll()
    }

    fun getOrderById(id: Long): OrderEntity {
        return orderRepository.findById(id).orElseThrow { IllegalArgumentException("Order not found with ID: $id") }
    }

    fun getOrdersByCustomer(customerId: Long): List<OrderEntity> {
        val user = userService.getUserById(customerId)
        return orderRepository.findByCustomerId(user)
    }

    @Transactional
    fun createOrder(orderRequest: OrderRequest): OrderEntity {
        // Fetch and validate the cart
        val cart = cartRepository.findById(orderRequest.cartId)
            .orElseThrow { IllegalArgumentException("Cart not found with ID: ${orderRequest.cartId}") }

        // Fetch and validate the customer
        val customer = userRepository.findById(orderRequest.customerId)
            .orElseThrow { IllegalArgumentException("Customer not found with ID: ${orderRequest.customerId}") }

        if (cart.items.isEmpty()) {
            throw IllegalStateException("Cart cannot be empty.")
        }

        val savedOrder = orderRepository.save(
            OrderEntity(
                customerId = customer,
                type = orderRequest.orderType,
                address = orderRequest.address,
                phone = orderRequest.phone,
                firstName = orderRequest.firstName,
                lastName = orderRequest.lastName,
                notes = orderRequest.orderNotes,
                wishedPickupTime = orderRequest.wishedPickupTime,
            )
        )

        // Map cart items to order items and save them
        val orderItems = cart.items.map { cartItem ->
            val productName = cartItem.product.translations.firstOrNull { it.language == orderRequest.language }?.name
                ?: cartItem.product.name
            val productDescription =
                cartItem.product.translations.firstOrNull { it.language == orderRequest.language }?.description
                    ?: cartItem.product.description

            orderItemRepository.save(
                OrderItemEntity(
                    order = savedOrder,
                    productId = cartItem.product.id,
                    productName = productName,
                    productDescription = productDescription,
                    productPrice = cartItem.product.price,
                    quantity = cartItem.quantity,
                    notes = cartItem.notes,
                    productImage = cartItem.product.image
                )
            )
        }


        savedOrder.items.addAll(orderItems)
        cartService.deleteCart(cart.id)
        return savedOrder.also {
            savedOrder.sendOrderConfirmationEmail()
            savedOrder.notifyAdmins()
        }
    }

    @Transactional
    fun createGuestOrder(orderRequest: GuestOrderRequest): OrderEntity {
        if (orderRequest.items.isEmpty()) {
            throw IllegalStateException("Order must contain at least one item.")
        }

        val savedOrder = orderRepository.save(
            OrderEntity(
                customerId = null,
                type = orderRequest.orderType,
                address = orderRequest.address,
                phone = orderRequest.phone,
                firstName = orderRequest.firstName,
                lastName = orderRequest.lastName,
                notes = orderRequest.orderNotes,
                wishedPickupTime = orderRequest.wishedPickupTime,
            )
        )

        val orderItems = orderRequest.items.map { item ->
            val product = productService.getProductsById(item.productId)
            val productName = product.translations.firstOrNull { it.language == orderRequest.language }?.name
                ?: product.name
            val productDescription = product.translations.firstOrNull { it.language == orderRequest.language }?.description
                ?: product.description

            orderItemRepository.save(
                OrderItemEntity(
                    order = savedOrder,
                    productId = product.id,
                    productName = productName,
                    productDescription = productDescription,
                    productPrice = product.price,
                    quantity = item.quantity,
                    notes = item.notes,
                    productImage = product.image,
                )
            )
        }

        savedOrder.items.addAll(orderItems)
        return savedOrder.also {
            savedOrder.notifyAdmins()
        }
    }

    fun updateOrderStatus(orderStatusRequest: OrderStatusRequest): OrderEntity {
        val order = orderRepository.findById(orderStatusRequest.orderId)
            .orElseThrow { IllegalArgumentException("Order not found with ID: ${orderStatusRequest.orderId}") }

        order.status = orderStatusRequest.status
        return orderRepository.save(order).also {
            when (orderStatusRequest.status) {
                OrderStatus.APPROVED -> {
                    notificationService.createNotification(
                        title = "تمت الموافقة على طلبك",
                        message = "تمت الموافقة على طلبك رقم ${order.id}",
                        userId = order.customerId?.id
                            ?: throw IllegalArgumentException("Order does not have a customer"),
                        orderId = order.id
                    )
                }

                OrderStatus.READY_FOR_PICKUP -> {
                    notificationService.createNotification(
                        title = "الطلب جاهز للاستلام",
                        message = "الطلب رقم ${order.id} جاهز للاستلام",
                        userId = order.customerId?.id
                            ?: throw IllegalArgumentException("Order does not have a customer"),
                        orderId = order.id
                    )
                }

                OrderStatus.PICKED_UP -> {
                    notificationService.createNotification(
                        title = "تم استلام الطلب",
                        message = "تم استلام الطلب رقم ${order.id}",
                        userId = order.customerId?.id
                            ?: throw IllegalArgumentException("Order does not have a customer"),
                        orderId = order.id
                    )
                }

                OrderStatus.DECLINED -> {
                    notificationService.createNotification(
                        title = "تم رفض طلبك",
                        message = "تم رفض طلبك رقم ${order.id}",
                        userId = order.customerId?.id
                            ?: throw IllegalArgumentException("Order does not have a customer"),
                        orderId = order.id
                    )
                }

                else -> {
                    // TODO add other order specific notifications
                }
            }
        }
    }

    fun cancelOrder(id: Long): OrderEntity {
        val order = orderRepository.findById(id).orElseThrow { IllegalArgumentException("Order not found with ID: $id") }
        if (order.status == OrderStatus.CANCELLED) {
            throw IllegalStateException("Order is already cancelled.")
        }
        if (!order.status.isCancelable()) {
            throw IllegalStateException("لا يمكن الغاء الطلب في هذه المرحلة، يرجى التواصل مع المخبز عبر الهاتف.")
        }
        order.status = OrderStatus.CANCELLED
        return orderRepository.save(order).also {
            notificationService.createNotification(
                title = "تم الغاء طلبك",
                message = "تم الغاء طلبك رقم ${order.id}",
                userId = order.customerId?.id
                    ?: throw IllegalArgumentException("Order does not have a customer"),
                orderId = order.id
            )
        }
    }

    private fun OrderStatus.isCancelable(): Boolean = this == OrderStatus.SUBMITTED || this == OrderStatus.APPROVED

    private fun OrderEntity.sendOrderConfirmationEmail() {
        customerId?.let {
            val arabicText: String = """
            <html>
            <body>
                <p>تم استلام طلبك بنجاح</p>
                <p>رقم الطلب: $id</p>
                <p>العنوان: $address</p>
                <p>رقم الهاتف: $phone</p>
                <p>${
                wishedPickupTime?.let { time ->
                    "الوقت المفضل للاستلام: ${time.format(formatter)}"
                } ?: " في أقرب وقت ممكن"
            }</p>
<p>المنتجات:</p>
<table border="1">
    <tr>
        <th>الكمية</th>
        <th>اسم المنتج</th>
        <th>السعر</th>
        <th>ملاحظات</th>
    </tr>
    ${
                items.joinToString("") { item ->
                    """
        <tr>
            <td>${item.quantity}</td>
            <td>${item.productName}</td>
            <td>${item.productPrice}₪</td>
            <td>${item.notes}</td>
        </tr>
        """.trimIndent()
                }
            }
</table>
            </body>
            </html>
        """.trimIndent()

            val hebrewText: String = """
            <html>
            <body>
                <p>ההזמנה שלך התקבלה בהצלחה</p>
                <p>מספר הזמנה: $id</p>
                <p>כתובת: $address</p>
                <p>מספר טלפון: $phone</p>
                <p>${
                wishedPickupTime?.let { time ->
                    "זמן איסוף מועדף: ${time.format(formatter)}"
                } ?: "בהקדם האפשרי"
            }</p>
<p>מוצרים:</p>
<table border="1">
    <tr>
        <th>כמות</th>
        <th>שם המוצר</th>
        <th>מחיר</th>
        <th>הערות</th>
    </tr>
    ${
                items.joinToString("") { item ->
                    """
        <tr>
            <td>${item.quantity}</td>
            <td>${item.productName}</td>
            <td>${item.productPrice}₪</td>
            <td>${item.notes}</td>
        </tr>
        """.trimIndent()
                }
            }
</table>
            </body>
            </html>
        """.trimIndent()

            val combinedText = "$arabicText<br><br>$hebrewText"

            if (it.emailVerified && it.agreeToReceiveMessages) {
                emailService.sendEmail(it.email, "تاكيد الطلب | אישור הזמנה", combinedText, isHtml = true)
            }
        }
    }

    private fun OrderEntity.notifyAdmins() {
        val admins = userService.getUsersByRole(UserRole.ADMIN)
        val text: String = """
            <html>
            <body>
                <p>هناك طلبيه جديده</p>
                <p>رقم الطلب: $id</p>
                <p>العميل: $firstName $lastName</p>
                <p>البريد الالكتروني: ${customerId?.email}</p>
                <p>العنوان: $address</p>
                <p>رقم الهاتف: $phone</p>
                <p>${
            wishedPickupTime?.let { time ->
                "الوقت المفضل للاستلام: ${time.format(formatter)}"
            } ?: "العميل يريد استلام الطلب في أقرب وقت ممكن"
        }</p>
<p>المنتجات:</p>
<table border="1">
    <tr>
        <th>الكمية</th>
        <th>اسم المنتج</th>
        <th>السعر</th>
        <th>ملاحظات</th>
    </tr>
    ${
            items.joinToString("") { item ->
                """
        <tr>
            <td>${item.quantity}</td>
            <td>${item.productName}</td>
            <td>${item.productPrice}₪</td>
            <td>${item.notes}</td>
        </tr>
        """.trimIndent()
            }
        }
</table>
            </body>
            </html>
        """.trimIndent()
        admins.filter { it.agreeToReceiveMessages }.forEach { admin ->
            emailService.sendEmail(admin.email, "طلبيه جديده", text, isHtml = true)
        }
        val telegramMessage = """
            هناك طلبيه جديده
            رقم الطلب: $id
            العميل: $firstName $lastName
            البريد الالكتروني: ${customerId?.email}
            العنوان: $address
            رقم الهاتف: $phone
            ${
            wishedPickupTime?.let { time ->
                "الوقت المفضل للاستلام: ${time.format(formatter)}"
            } ?: "العميل يريد استلام الطلب في أقرب وقت ممكن"
        }
            المنتجات:
            ${
            items.joinToString("\n") { item ->
                """
                الكمية: ${item.quantity}
                اسم المنتج: ${item.productName}
                السعر: ${item.productPrice}₪
                ملاحظات: ${item.notes}
                ----------------
                """.trimIndent()
            }
        }
        """.trimIndent()
        telegramAdminRepository.findAll().forEach {
            telegramBot.sendMessage(it.chatId, telegramMessage)
        }

    }
}
