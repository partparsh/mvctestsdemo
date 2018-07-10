package com.n26.transaction.repository

import com.n26.transaction.domain.Transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class TransactionRepositoryImpl : TransactionRepository {


    val logger: Logger = LoggerFactory.getLogger(TransactionRepositoryImpl::class.java)

    override fun getTransactionIdsOfSameType(type: String): ArrayList<Long>? {
        val filteredMap = Persists.filterOnType(type)
        if (filteredMap.isNotEmpty()) {
            logger.info("Type {} exist with keys {}", type, filteredMap.keys)
            return ArrayList(filteredMap.keys)
        } else {
            logger.info("Type {} does not exist", type)
            return null
        }
    }


    override fun putTransaction(transaction: Transaction): Transaction {
        if (Persists.exists(transaction.transaction_id)) {
            logger.info("Transaction with transaction_id {} exist, updating {}", transaction.transaction_id, transaction)
        } else {
            logger.info("Creating new transaction with transaction_id {}, adding {}", transaction.transaction_id, transaction)
        }
        return Persists.put(transaction)
    }


    override fun getTransaction(transactionId: Long): Transaction? {
        if (Persists.exists(transactionId)) {
            logger.info("Transaction with transaction_id {} exist", transactionId)
            return Persists.get(transactionId)
        } else {
            logger.info("Transaction with transaction_id {} does not exist", transactionId)
            return null
        }
    }

    override fun getChildTransactionsAmountList(transactionId: Long): ArrayList<Double>? {
        if (Persists.exists(transactionId)) {
            logger.info("Transaction with transaction_id {} exist", transactionId)
            val listOfAmount = arrayListOf<Double>()
            Persists.filterOnParentId(transactionId).values.parallelStream().forEach {
                listOfAmount.add(it.amount)
            }
            return listOfAmount
        } else {
            logger.info("Transaction with transaction_id {} does not exist", transactionId)
            return null
        }
    }

}