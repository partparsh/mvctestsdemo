package com.paresh.mvctestsdemo.dsl

import org.springframework.http.*
import org.springframework.web.client.RestTemplate
data class Transaction( var transaction_id: Long = 0, var amount: Double? = 0.0, var type: String? = "", var parent_id: Long?= null)
data class TransactionRequest(var amount: Double? = 0.0, var type: String? = "", var parent_id: Long?= null)
data class SumRequest(val sum: Double?)

fun apiTest(block: ApiTestExecutor.() -> Unit) {
    ApiTestExecutor().apply(block)
}

class ApiTestExecutor {
    private val URL = "http://localhost:8080"
    private var client: RestTemplate = RestTemplate()
    private var response: ResponseEntity<*>? = null

    fun createTransaction(block: Transaction.() -> Unit) {
        val transaction: Transaction = Transaction().apply(block)
        val transactionRequest = TransactionRequest(transaction.amount, transaction.type, transaction.parent_id)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(transactionRequest, headers)
        response = client.exchange("${URL}/transactionservice/transaction/${transaction.transaction_id}", HttpMethod.PUT, entity, Void::class.java)
    }
    fun getTransaction(block: Transaction.() -> Unit) {
        val transaction: Transaction = Transaction().apply(block)
        response = client.getForEntity("${URL}/transactionservice/transaction/${transaction.transaction_id}", TransactionRequest::class.java)
    }
    fun getType(block: Transaction.() -> Unit) {
        val transaction: Transaction = Transaction().apply(block)
        response = client.getForEntity("${URL}/transactionservice/types/${transaction.type}", Array<Long>::class.java)
    }
    fun getSum(block: Transaction.() -> Unit) {
        val transaction: Transaction = Transaction().apply(block)
        response = client.getForEntity("${URL}/transactionservice/sum/${transaction.transaction_id}", SumRequest::class.java)
    }
    fun verifyResponse(block: ResponseVerification.() -> Unit) {
        ResponseVerification(response).apply(block)
    }
}