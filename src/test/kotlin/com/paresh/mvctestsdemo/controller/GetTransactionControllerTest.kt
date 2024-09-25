package com.paresh.mvctestsdemo.controller

import com.paresh.mvctestsdemo.TestHelper
import com.paresh.mvctestsdemo.exception.TransactionNotFoundException
import com.paresh.mvctestsdemo.request.TransactionRequest
import com.paresh.mvctestsdemo.service.TransactionService
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import com.ninjasquad.springmockk.MockkBean

@RunWith(SpringRunner::class)
@WebMvcTest(TransactionController::class)
@ExtendWith(MockKExtension::class)
class GetTransactionControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var transactionService: TransactionService

    @Test
    fun getTransaction_badRequestTransactionIdNotLong() {
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/transaction/badRequest"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun getTransaction_notFoundTransactionIdNotSet() {
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/transaction/"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)

    }

    @Test
    fun getTransaction_notFoundNoTransactionExist() {
        every { transactionService.getTransaction((any<Long>())) } throws TransactionNotFoundException()
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/transaction/${TestHelper.randomTransactionId()}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun getTransaction_okRequestWithRequiredFields() {
        val transactionId = TestHelper.randomTransactionId()
        val request = TransactionRequest(100.01, "Test", null)
       every { transactionService.getTransaction(transactionId) } returns request
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/transaction/${transactionId}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("amount").value(request.amount))
            .andExpect(MockMvcResultMatchers.jsonPath("type").value(request.type))
            .andExpect(MockMvcResultMatchers.jsonPath("parent_id").doesNotExist())
    }

    @Test
    fun getTransaction_okReuqestWithAllFields() {
        val transactionId = TestHelper.randomTransactionId()
        val request = TransactionRequest(100.01, "Test", 10)
        every { transactionService.getTransaction(transactionId) } returns request
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/transaction/${transactionId}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("amount").value(request.amount))
            .andExpect(MockMvcResultMatchers.jsonPath("type").value(request.type))
            .andExpect(MockMvcResultMatchers.jsonPath("parent_id").value(request.parent_id!!))
    }
}