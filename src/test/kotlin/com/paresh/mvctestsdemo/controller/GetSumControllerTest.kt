package com.paresh.mvctestsdemo.controller

import com.ninjasquad.springmockk.MockkBean
import com.paresh.mvctestsdemo.TestHelper
import com.paresh.mvctestsdemo.exception.TransactionNotFoundException
import com.paresh.mvctestsdemo.request.SumRequest
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

@RunWith(SpringRunner::class)
@WebMvcTest(TransactionController::class)
@ExtendWith(MockKExtension::class)
class GetSumControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
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
        every { transactionService.getSum(any<Long>()) } throws TransactionNotFoundException()
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/sum/${TestHelper.randomTransactionId()}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun getSum_okRequestSum() {
        every { transactionService.getSum(any<Long>()) } returns SumRequest(10.001)
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/sum/${TestHelper.randomTransactionId()}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("sum").value(10.001))
    }
}