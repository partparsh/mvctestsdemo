package com.n26.transaction.repository

import com.n26.transaction.domain.Transaction

object Persists {
    var transactionMap = mutableMapOf<Long, Transaction>()

    fun put (transaction:Transaction): Transaction {
        transactionMap.put(transaction.transaction_id, transaction)
        return transaction
    }
    fun get (transactionId: Long): Transaction {
        return transactionMap.get(transactionId)!!
    }
    fun exists(transactionId: Long): Boolean {
        return transactionMap.containsKey(transactionId)
    }
    fun filterOnType(type: String): Map<Long, Transaction> {
        return transactionMap.filterValues {
            it.type.toLowerCase().trim() == type.toLowerCase().trim()
        }
    }
    fun filterOnParentId(transactionId: Long): Map<Long, Transaction>  {
        return  transactionMap.filter {
            it.key == transactionId || it.value.parent_id == transactionId
        }

    }
}