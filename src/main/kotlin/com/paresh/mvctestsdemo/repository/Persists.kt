package com.paresh.mvctestsdemo.repository

import com.paresh.mvctestsdemo.domain.Transaction

object Persists {
    var transactionMap = mutableMapOf<Long, Transaction>()

    fun put (transaction:Transaction): Transaction {
        transactionMap[transaction.transaction_id] = transaction
        return transaction
    }
    fun get (transactionId: Long): Transaction {
        return transactionMap[transactionId]!!
    }
    fun exists(transactionId: Long): Boolean {
        return transactionMap.containsKey(transactionId)
    }
    fun filterOnType(type: String): Map<Long, Transaction> {
        return transactionMap.filterValues {
            it.type.lowercase().trim() == type.lowercase().trim()
        }
    }
    fun filterOnParentId(transactionId: Long): Map<Long, Transaction>  {
        return  transactionMap.filter {
            it.key == transactionId || it.value.parent_id == transactionId
        }
    }
}