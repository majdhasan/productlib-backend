package com.meshhdawi.productlib.payment

import com.meshhdawi.productlib.AppProperties
import com.paypal.api.payments.Amount
import com.paypal.api.payments.Payer
import com.paypal.api.payments.Payment
import com.paypal.api.payments.PaymentExecution
import com.paypal.api.payments.RedirectUrls
import com.paypal.api.payments.Transaction
import com.paypal.base.rest.APIContext
import com.paypal.base.rest.PayPalRESTException
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val appProperties: AppProperties
) {
    private val clientId: String = appProperties.paypalConfig.clientId
    private val clientSecret: String = appProperties.paypalConfig.clientSecret
    private val mode: String = appProperties.paypalConfig.mode

    private val apiContext: APIContext = APIContext(clientId, clientSecret, mode)

    fun createPayment(
        total: Double,
        currency: String,
        method: String,
        intent: String,
        description: String,
        cancelUrl: String,
        successUrl: String
    ): Payment {
        val amount = Amount().apply {
            this.currency = currency
            this.total = String.format("%.2f", total)
        }

        val transaction = Transaction().apply {
            this.description = description
            this.amount = amount
        }

        val payer = Payer().apply {
            this.paymentMethod = method
        }

        val redirectUrls = RedirectUrls().apply {
            this.cancelUrl = cancelUrl
            this.returnUrl = successUrl
        }

        val payment = Payment().apply {
            this.intent = intent
            this.payer = payer
            this.transactions = listOf(transaction)
            this.redirectUrls = redirectUrls
        }

        return try {
            payment.create(apiContext)
        } catch (e: PayPalRESTException) {
            throw RuntimeException("Error occurred while creating payment", e)
        }
    }

    fun executePayment(paymentId: String, payerId: String): Payment {
        val payment = Payment().apply {
            this.id = paymentId
        }

        val paymentExecution = PaymentExecution().apply {
            this.payerId = payerId
        }

        return try {
            payment.execute(apiContext, paymentExecution)
        } catch (e: PayPalRESTException) {
            throw RuntimeException("Error occurred while executing payment", e)
        }
    }
}
