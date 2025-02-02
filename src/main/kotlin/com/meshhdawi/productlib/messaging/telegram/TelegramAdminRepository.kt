package com.meshhdawi.productlib.messaging.telegram

import org.springframework.data.jpa.repository.JpaRepository

interface TelegramAdminRepository : JpaRepository<TelegramAdminEntity, Long>
