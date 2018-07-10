package com.n26.transaction.controller

import com.n26.transaction.exception.TypeNotFoundException
import com.n26.transaction.service.TransactionService
import org.hamcrest.Matchers
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
class GetTypesControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var transactionService: TransactionService
    @Test
    fun getTransactionIdsOfSameType_notFoundTypeNotSet() {
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/types/"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun getTransactionIdsOfSameType_notFoundNoTypeExist() {
        BDDMockito.given(transactionService.getTransactionIdOfSameType(BDDMockito.anyString())).willThrow(TypeNotFoundException())
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/types/notfound"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }


    @Test
    fun getTransactionIdsOfSameType_okRequestListOfSingleTransactionIds() {
        val transactionIds = arrayListOf<Long>(10)
        BDDMockito.given(transactionService.getTransactionIdOfSameType("test")).willReturn(transactionIds)
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/types/test"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(transactionIds.size))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(Matchers.containsInAnyOrder(transactionIds[0].toInt())))
    }

    @Test
    fun getTransactionIdsOfSameType_okRequestListOfMultipleTransactionIds() {
        val transactionIds = arrayListOf<Long>(10, 20)
        BDDMockito.given(transactionService.getTransactionIdOfSameType("test")).willReturn(transactionIds)
        mockMvc.perform(MockMvcRequestBuilders.get("/transactionservice/types/test"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(transactionIds.size))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(Matchers.containsInAnyOrder(transactionIds[0].toInt(),
                        transactionIds[1].toInt())))
    }
}