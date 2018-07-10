package com.n26.transaction.service

import com.n26.transaction.domain.Transaction
import com.n26.transaction.request.SumRequest
import com.n26.transaction.request.TransactionRequest

interface TransactionService {
    fun convertTransactionRequest(transactionId: Long, request: TransactionRequest): Transaction
    fun convertTransaction(transaction: Transaction): TransactionRequest
    fun addOrUpdateTransaction(transaction: Transaction): Transaction
    fun getTransaction(transactionId: Long): TransactionRequest
    fun getTransactionIdOfSameType(type: String): ArrayList<Long>
    fun getSum(transactionId: Long): SumRequest
}