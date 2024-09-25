package com.paresh.mvctestsdemo.repository

import com.paresh.mvctestsdemo.domain.Transaction

interface TransactionRepository {
    fun putTransaction(transaction: Transaction): Transaction
    fun getTransaction(transactionId: Long): Transaction?
    fun getTransactionIdsOfSameType(type: String): ArrayList<Long>?
    fun getChildTransactionsAmountList(transactionId: Long): ArrayList<Double>?
}