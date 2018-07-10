package com.n26.transaction.repository

import com.n26.transaction.domain.Transaction

interface TransactionRepository {
    fun putTransaction(transaction: Transaction): Transaction
    fun getTransaction(transactionId: Long): Transaction?
    fun getTransactionIdsOfSameType(type: String): ArrayList<Long>?
    fun getChildTransactionsAmountList(transactionId: Long): ArrayList<Double>?
}