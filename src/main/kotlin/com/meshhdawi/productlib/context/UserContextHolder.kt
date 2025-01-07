package com.meshhdawi.productlib.context

import com.meshhdawi.productlib.context.UserContextHolder.getUserContext
import com.meshhdawi.productlib.users.UserRole

object UserContextHolder {
    private val userContext = ThreadLocal<UserContext>()

    fun setUserContext(context: UserContext) = userContext.set(context)
    fun getUserContext(): UserContext? = userContext.get()
    fun clear() = userContext.remove()
}

data class UserContext(
    val userId: Long,
    val role: UserRole
)

fun getUserId(): Long = getUserContext()!!.userId
fun getUserRole(): UserRole = getUserContext()!!.role