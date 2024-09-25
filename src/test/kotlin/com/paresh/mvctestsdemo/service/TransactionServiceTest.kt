package com.paresh.mvctestsdemo.service

import com.paresh.mvctestsdemo.TestHelper
import com.paresh.mvctestsdemo.domain.Transaction
import com.paresh.mvctestsdemo.exception.TransactionNotFoundException
import com.paresh.mvctestsdemo.exception.TransactionOwnParentException
import com.paresh.mvctestsdemo.exception.TypeNotFoundException
import com.paresh.mvctestsdemo.repository.TransactionRepository
import com.paresh.mvctestsdemo.request.SumRequest
import com.paresh.mvctestsdemo.request.TransactionRequest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentMatchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.anyLong
import org.mockito.BDDMockito.anyString

@ExtendWith(MockKExtension::class)
class TransactionServiceTest {

    @MockK
    lateinit var transactionRepository: TransactionRepository

    lateinit var transactionService: TransactionService

    @BeforeEach
    fun setUp() {
        transactionService = TransactionServiceImpl(transactionRepository)
    }

    @Test
    fun addOrUpdateTransaction_transactionWithRequiredFields() {
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.0, "TEST", null)
        every { transactionRepository.putTransaction(transaction) } returns transaction
        Assertions.assertThat(transactionService.addOrUpdateTransaction(transaction)).isEqualTo(transaction)
    }

    @Test
    fun addOrUpdateTransaction_transactionWithAllFields() {
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.0, "TEST", 10)
        every { transactionRepository.putTransaction(transaction) } returns transaction
        Assertions.assertThat(transactionService.addOrUpdateTransaction(transaction)).isEqualTo(transaction)
    }

    @Test
    fun addOrUpdateTransaction_transactionItsOwnParent() {
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.0, "TEST", transactionId)
        assertThrows<TransactionOwnParentException> {
            transactionService.addOrUpdateTransaction(transaction)
        }
    }

    @Test
    fun getTransaction_returnTransactionRequiredFields() {
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.0, "TEST", null)
        val transactionRequest = TransactionRequest(100.0, "TEST", null)
        every { transactionRepository.getTransaction(transactionId) } returns transaction
        Assertions.assertThat(transactionService.getTransaction(transactionId)).isEqualTo(transactionRequest)
    }

    @Test
    fun getTransaction_returnTransactionAllFields() {
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.0, "TEST", 10)
        val transactionRequest = TransactionRequest(100.0, "TEST", 10)
        every { transactionRepository.getTransaction(transactionId) } returns transaction
        Assertions.assertThat(transactionService.getTransaction(transactionId)).isEqualTo(transactionRequest)
    }

    @Test
    fun getTransaction_transactionNotFound() {
        every { transactionRepository.getTransaction(any<Long>()) } returns null
        assertThrows<TransactionNotFoundException> {
            transactionService.getTransaction(anyLong())
        }
    }

    @Test
    fun getTransactionIdOfSameType_typeNotFound() {
        every { transactionRepository.getTransactionIdsOfSameType(any<String>()) } returns null
        assertThrows<TypeNotFoundException> {
            transactionService.getTransactionIdOfSameType(anyString())
        }
    }

    @Test
    fun getTransactionIdOfSameType_returnArrayListTransactionIds() {
        val transactionIds = arrayListOf<Long>(10, 20)
        every { transactionRepository.getTransactionIdsOfSameType(any<String>()) } returns transactionIds
        Assertions.assertThat(transactionService.getTransactionIdOfSameType(anyString())).isEqualTo(transactionIds)
    }

    @Test()
    fun getSum_transactionNotFound() {
        every { transactionRepository.getChildTransactionsAmountList(any<Long>()) } returns null
        assertThrows<TransactionNotFoundException> {
            transactionService.getSum(anyLong())
        }
    }

    @Test
    fun getSum_noChildTransactionReturnsAmount() {
        val amount = arrayListOf<Double>(10.0)
        every { transactionRepository.getChildTransactionsAmountList(any<Long>()) } returns amount
        Assertions.assertThat(transactionService.getSum(ArgumentMatchers.anyLong())).isEqualTo(SumRequest(amount[0]))
    }

    @Test
    fun getSum_childTransactionsReturnsSum() {
        val amount = arrayListOf<Double>(15.10, -5.09)
        every { transactionRepository.getChildTransactionsAmountList(any<Long>())} returns amount
        Assertions.assertThat(transactionService.getSum(ArgumentMatchers.anyLong())).isEqualTo(SumRequest(
            amount[0] + amount[1])
        )
    }

}