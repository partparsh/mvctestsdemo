package com.paresh.mvctestsdemo.service

import com.paresh.mvctestsdemo.domain.Transaction
import com.paresh.mvctestsdemo.request.SumRequest
import com.paresh.mvctestsdemo.request.TransactionRequest

interface TransactionService {
    fun convertTransactionRequest(transactionId: Long, request: TransactionRequest): Transaction
    fun convertTransaction(transaction: Transaction): TransactionRequest
    fun addOrUpdateTransaction(transaction: Transaction): Transaction
    fun getTransaction(transactionId: Long): TransactionRequest
    fun getTransactionIdOfSameType(type: String): ArrayList<Long>
    fun getSum(transactionId: Long): SumRequest
}