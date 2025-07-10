package com.meshhdawi.productlib.orders

import com.meshhdawi.productlib.users.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<OrderEntity, Long> {

    fun findByCustomerId(customerId: UserEntity): List<OrderEntity>

    fun findByIdAndLastNameAndCustomerIdIsNull(id: Long, lastName: String): OrderEntity?
}
