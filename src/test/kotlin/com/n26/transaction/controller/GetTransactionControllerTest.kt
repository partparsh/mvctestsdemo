package com.n26.transaction.controller

import com.n26.transaction.TestHelper
import com.n26.transaction.exception.TransactionNotFoundException
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
class GetTransactionControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
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
        BDDMockito.given(transactionService.getTransaction(BDDMockito.anyLong())).willThrow(TransactionNotFoundException())
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/transaction/${TestHelper.randomTransactionId()}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun getTransaction_okRequestWithRequiredFields() {
        val transactionId = TestHelper.randomTransactionId()
        val request = TransactionRequest(100.01, "Test", null)
        BDDMockito.given(transactionService.getTransaction(transactionId)).willReturn(request)
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/transaction/${transactionId}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("amount").value(request.amount))
                .andExpect(MockMvcResultMatchers.jsonPath("type").value(request.type))
                .andExpect(MockMvcResultMatchers.jsonPath("parent_id").doesNotExist())
    }

    @Test
    fun getTransaction_okReuqestWithAllFields() {
        val transactionId = TestHelper.randomTransactionId()
        val request = TransactionRequest(100.01, "Test", 10)
        BDDMockito.given(transactionService.getTransaction(transactionId)).willReturn(request)
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/transaction/${transactionId}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("amount").value(request.amount))
                .andExpect(MockMvcResultMatchers.jsonPath("type").value(request.type))
                .andExpect(MockMvcResultMatchers.jsonPath("parent_id").value(request.parent_id!!))
    }
}