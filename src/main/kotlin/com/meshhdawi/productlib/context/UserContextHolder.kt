package com.meshhdawi.productlib.context

import com.meshhdawi.productlib.context.UserContextHolder.getUserContext

object UserContextHolder {
    private val userContext = ThreadLocal<UserContext>()

    fun setUserContext(context: UserContext) {
        userContext.set(context)
    }

    fun getUserContext(): UserContext? {
        return userContext.get()
    }

    fun clear() {
        userContext.remove()
    }
}

data class UserContext(
    val userId: Long
)

fun getUserId(): Long {
    return getUserContext()!!.userId
}