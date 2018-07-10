package com.n26.transaction.controller

import com.n26.transaction.TestHelper
import com.n26.transaction.exception.TransactionNotFoundException
import com.n26.transaction.request.SumRequest
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
class GetSumControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var transactionService: TransactionService


    @Test
    fun getSum_badRequestTransactionIdNotLong() {
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/sum/badRequest"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun getSum_notFoundTransactionIdNotSet() {
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/sum/"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun getSum_notFoundNoTransactionExist() {
        BDDMockito.given(transactionService.getSum(BDDMockito.anyLong())).willThrow(TransactionNotFoundException())
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/sum/${TestHelper.randomTransactionId()}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun getSum_okRequestSum() {
        BDDMockito.given(transactionService.getSum(BDDMockito.anyLong())).willReturn(SumRequest(10.001))
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/sum/${TestHelper.randomTransactionId()}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("sum").value(10.001))
    }

}