package com.paresh.mvctestsdemo.service

import com.paresh.mvctestsdemo.domain.Transaction
import com.paresh.mvctestsdemo.exception.TransactionNotFoundException
import com.paresh.mvctestsdemo.exception.TransactionOwnParentException
import com.paresh.mvctestsdemo.exception.TypeNotFoundException
import com.paresh.mvctestsdemo.repository.TransactionRepository
import com.paresh.mvctestsdemo.request.SumRequest
import com.paresh.mvctestsdemo.request.TransactionRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.Assert

@Service
class TransactionServiceImpl(var transactionRepository: TransactionRepository) : TransactionService {
    val logger: Logger = LoggerFactory.getLogger(TransactionService::class.java)

    override fun convertTransactionRequest(transactionId: Long, request: TransactionRequest): Transaction {
        Assert.notNull(transactionId, "transaction_id cannot be null")
        Assert.notNull(request.amount, "amount cannot be null")
        Assert.notNull(request.type, "type cannot be null")
        return Transaction(transactionId, request.amount, request.type, request.parent_id)
    }

    override fun convertTransaction(transaction: Transaction): TransactionRequest {
        Assert.notNull(transaction.amount, "amount cannot be null")
        Assert.notNull(transaction.type, "type cannot be null")
        return TransactionRequest(transaction.amount, transaction.type, transaction.parent_id)
    }

    override fun addOrUpdateTransaction(transaction: Transaction): Transaction {
        if(transaction.transaction_id == transaction.parent_id) {
            logger.info("Transaction cannot have same transaction_id {} and parent_id {}", transaction.transaction_id, transaction.parent_id)
            throw TransactionOwnParentException()
        }
        return transactionRepository.putTransaction(transaction)
    }

    override fun getTransaction(transactionId: Long): TransactionRequest {
        val transaction: Transaction = transactionRepository.getTransaction(transactionId) ?: throw TransactionNotFoundException()
        logger.info("Transaction with transaction_id {} and {}", transactionId, transaction)
        return convertTransaction(transaction)
    }

    override fun getTransactionIdOfSameType(type: String): ArrayList<Long> {
        val transactionIds: ArrayList<Long> = transactionRepository.getTransactionIdsOfSameType(type)
            ?: throw TypeNotFoundException()
        logger.info("Transactions {} has type {}", transactionIds, type)
        return transactionIds
    }

    override fun getSum(transactionId: Long): SumRequest {
        val childTranAmounts: ArrayList<Double> = transactionRepository.getChildTransactionsAmountList(transactionId)
            ?: throw TransactionNotFoundException()
        if (childTranAmounts.size == 1) {
            return SumRequest(childTranAmounts[0])
        }
        return SumRequest(childTranAmounts.sum())
    }
}