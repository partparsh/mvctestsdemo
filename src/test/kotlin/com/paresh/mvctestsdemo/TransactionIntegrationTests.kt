package com.paresh.mvctestsdemo

import com.paresh.mvctestsdemo.domain.Transaction
import com.paresh.mvctestsdemo.repository.Persists
import com.paresh.mvctestsdemo.request.SumRequest
import com.paresh.mvctestsdemo.request.TransactionRequest
import  org.assertj.core.api.Assertions as JsonAssertion
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.http.*
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import kotlin.test.assertContentEquals

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionIntegrationTests {


    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Before
    @After
    fun cleanPersists () {
        Persists.transactionMap.clear()
    }


    @Test
    fun putTransaction_addTransaction() {
        //arrange
        val transactionId = TestHelper.randomTransactionId()
        val transaction = TransactionRequest(100.1, "Test", null)
        val transactionJson: String = TestHelper.convertToJson(transaction)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity: HttpEntity<String> = HttpEntity<String>(transactionJson, headers)

        //act
        val response = restTemplate.exchange("/transactionservice/transaction/{transactionId}",
            HttpMethod.PUT, entity, String::class.java, transactionId)

        //react
        Assertions.assertEquals(response.statusCode, HttpStatus.OK)
        Assertions.assertTrue(Persists.transactionMap.containsKey(transactionId))
        val persist: Transaction? = Persists.transactionMap[transactionId]
        Assertions.assertEquals(persist?.amount, transaction.amount)
        Assertions.assertEquals(persist?.type, transaction.type)
        Assertions.assertEquals(persist?.parent_id, transaction.parent_id)
    }

    @Test
    fun putTransaction_updateTransaction() {
        //arrange
        val transactionId = TestHelper.randomTransactionId()
        Persists.transactionMap[transactionId] = Transaction(transactionId, 100.1, "Test", null)

        val transaction = TransactionRequest(-5000.0, "UPDATED_VALUE", 10)
        val transactionJson: String = TestHelper.convertToJson(transaction)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity: HttpEntity<String> = HttpEntity<String>(transactionJson, headers)

        //act
        val response = restTemplate.exchange("/transactionservice/transaction/{transactionId}",
            HttpMethod.PUT, entity, String::class.java, transactionId)

        //react
        Assertions.assertEquals(response.statusCode, HttpStatus.OK)
        Assertions.assertTrue(Persists.transactionMap.containsKey(transactionId))
        val persist: Transaction = Persists.transactionMap[transactionId]!!
        Assertions.assertEquals(persist.amount, transaction.amount)
        Assertions.assertEquals(persist.type, transaction.type)
        Assertions.assertEquals(persist.parent_id, transaction.parent_id)
    }

        @Test
        fun getTransaction_getTransactionDetailsRequiredFields() {
            //arrange
            val transactionId = TestHelper.randomTransactionId()
            val transaction = Transaction(transactionId, 100.1, "Test", null)
            Persists.transactionMap[transactionId] = transaction
            //act
            val response = restTemplate.getForEntity("/transactionservice/transaction/{transactionId}",
                String::class.java, transactionId)

            //react
            Assertions.assertEquals(response.statusCode, HttpStatus.OK)
            JsonAssertion.assertThat(jsonPath("amount").value(transaction.amount))
            JsonAssertion.assertThat(jsonPath("type").value(transaction.type))
            JsonAssertion.assertThat(jsonPath("parent_id").doesNotExist())
        }

        @Test
        fun getTransaction_getTransactionDetailsAllFields() {
            //arrange
            val transactionId = TestHelper.randomTransactionId()
            val transaction = Transaction(transactionId, 100.1, "Test", 10)
            Persists.transactionMap[transactionId] = transaction
            //act
            val response = restTemplate.getForEntity("/transactionservice/transaction/{transactionId}",
                String::class.java, transactionId)

            //react
            Assertions.assertEquals(response.statusCode, HttpStatus.OK)
            JsonAssertion.assertThat(jsonPath("amount").value(transaction.amount))
            JsonAssertion.assertThat(jsonPath("type").value(transaction.type))
            JsonAssertion.assertThat(jsonPath("parent_id").value(transaction.parent_id!!))
        }

        @Test
        fun getTransactionIdsSameType_listOfTransactionIdsWithSameType() {
            //arrange
            val type = "cars"
            val keys = longArrayOf(TestHelper.randomTransactionId(), TestHelper.randomTransactionId())
            for (key in keys) {
                Persists.transactionMap[key] = Transaction(key, 100.1, "cars", 10)
            }
            //act
            val response = restTemplate.getForEntity("/transactionservice/types/{types}", LongArray::class.java, type)

            //react
            Assertions.assertEquals(response.statusCode, HttpStatus.OK)
            assertContentEquals(response.body, keys)

        }

        @Test
        fun getTransactionIdsSameType_singleTransactionIdDifferentTypes() {

            //arrange
            val tranMap = HashMap<String, Long>()
            tranMap["house"] = TestHelper.randomTransactionId()
            tranMap["car"] = TestHelper.randomTransactionId()

            for (tran in tranMap) {
                Persists.transactionMap[tran.value] = Transaction(tran.value, 100.1, tran.key, 10)
            }

            //act
            for (tran in tranMap) {
                val response = restTemplate.getForEntity("/transactionservice/types/{types}",
                    LongArray::class.java, tran.key)

                //react
                Assertions.assertEquals(response.statusCode, HttpStatus.OK)
                assertContentEquals(response.body, longArrayOf(tran.value))
            }
        }

        @Test
        fun getSum_sumEqualsToAmountWhenNoParentId() {

            //arrange
            val tranMap = HashMap<Long, SumRequest>()
            tranMap[TestHelper.randomTransactionId()] = SumRequest(100.01)
            tranMap[TestHelper.randomTransactionId()] = SumRequest(-214.01)

            for (tran in tranMap) {
                Persists.transactionMap[tran.key] = Transaction(tran.key, tran.value.sum, "Test", null)
            }

            for (tran in tranMap) {
                //act
                val response = restTemplate.getForEntity("/transactionservice/sum/{transaction_id}", String::class.java,
                    tran.key)
                //response
                Assertions.assertEquals(response.statusCode, HttpStatus.OK)
                Assertions.assertEquals(response.body, TestHelper.convertToJson(tran.value))
            }
        }

        @Test
        fun getSum_sumOfTransitiveTransactionsParentId() {

            //arrange
            val tranMapParent = HashMap<Long, Transaction>()
            val tranMapChild = HashMap<Long, Transaction>()
            (0..1).forEach {
                val transactionId: Long = it.toLong()
                tranMapParent[transactionId] = Transaction(transactionId, 100.0, "Parent", null)
            }
            Persists.transactionMap.putAll(tranMapParent)
            (2..3).forEach {
                val transactionId: Long = it.toLong()
                tranMapChild[transactionId] = Transaction(transactionId, it * 100.0, "Child0", 0)
            }
            Persists.transactionMap.putAll(tranMapChild)
            val total: Double = tranMapParent[0]!!.amount + tranMapChild[2]!!.amount + tranMapChild[3]!!.amount
            //act
            val response = restTemplate.getForEntity("/transactionservice/sum/{transaction_id}", String::class.java,
                0)
            //response
            Assertions.assertEquals(response.statusCode, HttpStatus.OK)
            Assertions.assertEquals(response.body, TestHelper.convertToJson(SumRequest(total)))
        }
}