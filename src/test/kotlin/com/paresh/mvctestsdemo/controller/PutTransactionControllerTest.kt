package com.paresh.mvctestsdemo.controller

import com.ninjasquad.springmockk.MockkBean
import com.paresh.mvctestsdemo.TestHelper
import com.paresh.mvctestsdemo.domain.Transaction
import com.paresh.mvctestsdemo.exception.TransactionOwnParentException
import com.paresh.mvctestsdemo.request.TransactionRequest
import com.paresh.mvctestsdemo.service.TransactionService
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.http.MediaType

@RunWith(SpringRunner::class)
@WebMvcTest(TransactionController::class)
@ExtendWith(MockKExtension::class)
class PutTransactionControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
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
        every {
            transactionService.addOrUpdateTransaction(transactionService.convertTransactionRequest(transactionId, request))
        } throws TransactionOwnParentException()
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
        every {
            transactionService.addOrUpdateTransaction(
                transactionService.convertTransactionRequest(transactionId, request)
            )
        } returns Transaction(transactionId, 100.01, "testType", null)
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
        every {  transactionService.addOrUpdateTransaction(
            transactionService.convertTransactionRequest(transactionId, request))
        } returns Transaction(transactionId, 100.01, "testType", 11)
        mockMvc.perform(MockMvcRequestBuilders
            .put("/transactionservice/transaction/${transactionId}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}