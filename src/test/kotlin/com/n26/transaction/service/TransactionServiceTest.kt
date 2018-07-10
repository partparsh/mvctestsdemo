package com.n26.transaction.service

import com.n26.transaction.TestHelper
import com.n26.transaction.domain.Transaction
import com.n26.transaction.exception.TransactionNotFoundException
import com.n26.transaction.exception.TransactionOwnParentException
import com.n26.transaction.exception.TypeNotFoundException
import com.n26.transaction.repository.TransactionRepository
import com.n26.transaction.repository.TransactionRepositoryImpl
import com.n26.transaction.request.SumRequest
import com.n26.transaction.request.TransactionRequest
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TransactionServiceTest {
    @Mock
    var transactionRepository: TransactionRepository = TransactionRepositoryImpl()

    lateinit var transactionService: TransactionService

    @Before
    fun setUp() {
        transactionService = TransactionServiceImpl(transactionRepository)
    }

    @Test
    fun addOrUpdateTransaction_transactionWithRequiredFields() {
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.0, "TEST", null)
        Mockito.`when`(transactionRepository.putTransaction(transaction)).thenReturn(transaction)
        Assertions.assertThat(transactionService.addOrUpdateTransaction(transaction)).isEqualTo(transaction)
    }

    @Test
    fun addOrUpdateTransaction_transactionWithAllFields() {
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.0, "TEST", 10)
        Mockito.`when`(transactionRepository.putTransaction(transaction)).thenReturn(transaction)
        Assertions.assertThat(transactionService.addOrUpdateTransaction(transaction)).isEqualTo(transaction)
    }

    @Test(expected = TransactionOwnParentException::class)
    fun addOrUpdateTransaction_transactionItsOwnParent() {
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.0, "TEST", transactionId)
        transactionService.addOrUpdateTransaction(transaction)
    }

    @Test
    fun getTransaction_returnTransactionRequiredFields() {
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.0, "TEST", null)
        val transactionRequest = TransactionRequest(100.0, "TEST", null)
        Mockito.`when`(transactionRepository.getTransaction(transactionId)).thenReturn(transaction)
        Assertions.assertThat(transactionService.getTransaction(transactionId)).isEqualTo(transactionRequest)
    }

    @Test
    fun getTransaction_returnTransactionAllFields() {
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.0, "TEST", 10)
        val transactionRequest = TransactionRequest(100.0, "TEST", 10)
        Mockito.`when`(transactionRepository.getTransaction(transactionId)).thenReturn(transaction)
        Assertions.assertThat(transactionService.getTransaction(transactionId)).isEqualTo(transactionRequest)
    }

    @Test(expected = TransactionNotFoundException::class)
    fun getTransaction_transactionNotFound() {
        Mockito.`when`(transactionRepository.getTransaction(anyLong())).thenReturn(null)
        transactionService.getTransaction(anyLong())
    }

    @Test(expected = TypeNotFoundException::class)
    fun getTransactionIdOfSameType_typeNotFound() {
        given(transactionRepository.getTransactionIdsOfSameType(anyString())).willReturn(null)
        transactionService.getTransactionIdOfSameType(anyString())
    }

    @Test
    fun getTransactionIdOfSameType_returnArrayListTransactionIds() {
        val transactionIds = arrayListOf<Long>(10, 20)
        given(transactionRepository.getTransactionIdsOfSameType(anyString())).willReturn(transactionIds)
        Assertions.assertThat(transactionService.getTransactionIdOfSameType(anyString())).isEqualTo(transactionIds)
    }

    @Test(expected = TransactionNotFoundException::class)
    fun getSum_transactionNotFound() {
        Mockito.`when`(transactionRepository.getChildTransactionsAmountList(anyLong())).thenReturn(null)
        transactionService.getSum(anyLong())
    }

    @Test
    fun getSum_noChildTransactionReturnsAmount() {
        val amount = arrayListOf<Double>(10.0)
        given(transactionRepository.getChildTransactionsAmountList(anyLong())).willReturn(amount)
        Assertions.assertThat(transactionService.getSum(ArgumentMatchers.anyLong())).isEqualTo(SumRequest(amount[0]))
    }

    @Test
    fun getSum_childTransactionsReturnsSum() {
        val amount = arrayListOf<Double>(15.10, -5.09)
        given(transactionRepository.getChildTransactionsAmountList(anyLong())).willReturn(amount)
        Assertions.assertThat(transactionService.getSum(ArgumentMatchers.anyLong())).isEqualTo(SumRequest(
                amount[0] + amount[1]))
    }


}