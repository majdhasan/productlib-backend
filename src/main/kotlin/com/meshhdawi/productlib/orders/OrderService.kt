package com.meshhdawi.productlib.orders

import com.meshhdawi.productlib.cart.CartRepository
import com.meshhdawi.productlib.cart.CartService
import com.meshhdawi.productlib.messaging.email.EmailService
import com.meshhdawi.productlib.orders.orderitems.OrderItemEntity
import com.meshhdawi.productlib.orders.orderitems.OrderItemRepository
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
    private val emailService: EmailService
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

    fun updateOrderStatus(orderStatusRequest: OrderStatusRequest): OrderEntity {
        val order = orderRepository.findById(orderStatusRequest.orderId)
            .orElseThrow { IllegalArgumentException("Order not found with ID: ${orderStatusRequest.orderId}") }

        order.status = orderStatusRequest.status
        return orderRepository.save(order)
    }

    fun OrderEntity.sendOrderConfirmationEmail() {
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

    fun OrderEntity.notifyAdmins() {
        val admins = userRepository.findByRole(UserRole.ADMIN)
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
        admins.forEach { admin ->
            emailService.sendEmail(admin.email, "طلبيه جديده", text, isHtml = true)
        }
    }
}
