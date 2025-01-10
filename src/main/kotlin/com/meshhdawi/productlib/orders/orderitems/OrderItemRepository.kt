package com.meshhdawi.productlib.orders.orderitems

import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItemEntity, Long> {

}
