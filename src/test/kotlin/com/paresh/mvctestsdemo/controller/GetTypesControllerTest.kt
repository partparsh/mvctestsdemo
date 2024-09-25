package com.paresh.mvctestsdemo.controller

import com.ninjasquad.springmockk.MockkBean
import com.paresh.mvctestsdemo.exception.TypeNotFoundException
import com.paresh.mvctestsdemo.service.TransactionService
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.http.MediaType
import org.hamcrest.Matchers
import org.junit.jupiter.api.extension.ExtendWith

@RunWith(SpringRunner::class)
@WebMvcTest(TransactionController::class)
@ExtendWith(MockKExtension::class)
class GetTypesControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var transactionService: TransactionService

    @Test
    fun getTransactionIdsOfSameType_notFoundTypeNotSet() {
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/types/"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun getTransactionIdsOfSameType_notFoundNoTypeExist() {
        every { transactionService.getTransactionIdOfSameType(any<String>()) } throws TypeNotFoundException()
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/types/notfound"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }


    @Test
    fun getTransactionIdsOfSameType_okRequestListOfSingleTransactionIds() {
        val transactionIds = arrayListOf<Long>(10)
        every { transactionService.getTransactionIdOfSameType("test") } returns transactionIds
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/types/test"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(transactionIds.size))
            .andExpect(MockMvcResultMatchers.jsonPath("$").value(Matchers.containsInAnyOrder(transactionIds[0].toInt())))
    }

    @Test
    fun getTransactionIdsOfSameType_okRequestListOfMultipleTransactionIds() {
        val transactionIds = arrayListOf<Long>(10, 20)
        every { transactionService.getTransactionIdOfSameType("test") } returns transactionIds
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/types/test"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(transactionIds.size))
            .andExpect(MockMvcResultMatchers.jsonPath("$").value(Matchers.containsInAnyOrder(transactionIds[0].toInt(),
                transactionIds[1].toInt())))
    }
}