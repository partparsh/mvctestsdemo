package com.n26.transaction.controller

import com.n26.transaction.TestHelper
import com.n26.transaction.domain.Transaction
import com.n26.transaction.exception.TransactionOwnParentException
import com.n26.transaction.request.TransactionRequest
import com.n26.transaction.service.TransactionService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@WebMvcTest(TransactionController::class)
class PutTransactionControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var transactionService: TransactionService

    @Test
    fun putTransaction_badRequestAmountNotDouble() {
        val payload: String = TestHelper.convertToJson(
                hashMapOf("amount" to "InvalidAmount", "type" to "Test", "parent_id" to 10))
        mockMvc.perform(MockMvcRequestBuilders
                .put("/transactionservice/transaction/${TestHelper.randomTransactionId()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun putTransaction_badRequestParentIdNotLong() {
        val payload: String = TestHelper.convertToJson(
                hashMapOf("amount" to 100.0, "type" to "Test", "parent_id" to "Invalid"))
        mockMvc.perform(MockMvcRequestBuilders
                .put("/transactionservice/transaction/${TestHelper.randomTransactionId()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun putTransaction_badRequestTransactionIdNotLong() {
        val payload: String = TestHelper.convertToJson(TransactionRequest(10.0, "Test", 10))
        mockMvc.perform(MockMvcRequestBuilders
                .put("/transactionservice/transaction/badRequest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun putTransaction_notFoundTransactionIdNotSet() {
        val payload: String = TestHelper.convertToJson(TransactionRequest(10.0, "Test", 10))
        mockMvc.perform(MockMvcRequestBuilders
                .put("/transactionservice/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun putTransaction_badRequestNoBodySet() {
        mockMvc.perform(MockMvcRequestBuilders
                .put("/transactionservice/transaction/${TestHelper.randomTransactionId()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun putTransaction_badRequestTypeNotSet() {
        val payload: String = TestHelper.convertToJson(hashMapOf("amount" to 10.0, "parent_id" to 10))
        mockMvc.perform(MockMvcRequestBuilders
                .put("/transactionservice/transaction/${TestHelper.randomTransactionId()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    /*
    * This test is failing because jackson is de-serializing amount=null to amount=0.0 by default
    * */

    @Test
    fun putTransaction_badRequestAmountNotSet() {
        val payload: String = TestHelper.convertToJson(hashMapOf("type" to "Test", "parent_id" to 10))
        mockMvc.perform(MockMvcRequestBuilders
                .put("/transactionservice/transaction/${TestHelper.randomTransactionId()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun putTransaction_badRequestTransactionOwnParent() {
        val transactionId = TestHelper.randomTransactionId()
        val request = TransactionRequest(100.0, "TEST", transactionId)
        BDDMockito.given(transactionService.addOrUpdateTransaction(
                transactionService.convertTransactionRequest(transactionId, request)))
                .willThrow(TransactionOwnParentException())
        mockMvc.perform(MockMvcRequestBuilders
                .put("/transactionservice/transaction/${transactionId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestHelper.convertToJson(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }


    @Test
    fun putTransaction_okRequestRequiredFieldsArePresent() {
        val transactionId: Long = TestHelper.randomTransactionId()
        val request = TransactionRequest(100.01, "TestType", null)
        val payload: String = TestHelper.convertToJsonAndRemoveEntries(request, arrayOf("parent_id"))
        BDDMockito.given(transactionService.addOrUpdateTransaction(
                transactionService.convertTransactionRequest(transactionId, request)))
                .willReturn(Transaction(transactionId, 100.01, "testType", null))
        mockMvc.perform(MockMvcRequestBuilders
                .put("/transactionservice/transaction/${transactionId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun putTransaction_okRequestAllFieldsArePresent() {
        val transactionId: Long = TestHelper.randomTransactionId()
        val request = TransactionRequest(100.01, "TestType", 11)
        val payload: String = TestHelper.convertToJson(request)
        BDDMockito.given(transactionService.addOrUpdateTransaction(
                transactionService.convertTransactionRequest(transactionId, request)))
                .willReturn(Transaction(transactionId, 100.01, "testType", 11))
        mockMvc.perform(MockMvcRequestBuilders
                .put("/transactionservice/transaction/${transactionId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

}