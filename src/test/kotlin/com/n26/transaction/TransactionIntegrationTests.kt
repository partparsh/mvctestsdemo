package com.n26.transaction

import com.n26.transaction.domain.Transaction
import com.n26.transaction.repository.Persists
import com.n26.transaction.request.SumRequest
import com.n26.transaction.request.TransactionRequest
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath

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
        val transaction: TransactionRequest = TransactionRequest(100.1, "Test", null)
        val transactionJson: String = TestHelper.convertToJson(transaction)
        val headers: HttpHeaders = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity: HttpEntity<String> = HttpEntity<String>(transactionJson, headers)

        //act
        val response = restTemplate.exchange("/transactionservice/transaction/{transactionId}",
                HttpMethod.PUT, entity, String::class.java, transactionId)

        //react
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(Persists.transactionMap.containsKey(transactionId)).isTrue()
        val persist: Transaction? = Persists.transactionMap.get(transactionId)
        Assertions.assertThat(persist?.amount).isEqualTo(transaction.amount)
        Assertions.assertThat(persist?.type).isEqualTo(transaction.type)
        Assertions.assertThat(persist?.parent_id).isEqualTo(transaction.parent_id)

    }

    @Test
    fun putTransaction_updateTransaction() {

        //arrange
        val transactionId = TestHelper.randomTransactionId()
        Persists.transactionMap.put(transactionId, Transaction(transactionId, 100.1, "Test", null))

        val transaction = TransactionRequest(-5000.0, "UPDATED_VALUE", 10)
        val transactionJson: String = TestHelper.convertToJson(transaction)
        val headers: HttpHeaders = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity: HttpEntity<String> = HttpEntity<String>(transactionJson, headers)

        //act
        val response = restTemplate.exchange("/transactionservice/transaction/{transactionId}",
                HttpMethod.PUT, entity, String::class.java, transactionId)

        //react
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(Persists.transactionMap.containsKey(transactionId)).isTrue()

        val persist: Transaction = Persists.transactionMap.get(transactionId)!!
        Assertions.assertThat(persist.amount).isEqualTo(transaction.amount)
        Assertions.assertThat(persist.type).isEqualTo(transaction.type)
        Assertions.assertThat(persist.parent_id).isEqualTo(transaction.parent_id)
    }

    @Test
    fun getTransaction_getTransactionDetailsRequiredFields() {
        //arrange
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.1, "Test", null)
        Persists.transactionMap.put(transactionId, transaction)
        //act
        val response = restTemplate.getForEntity("/transactionservice/transaction/{transactionId}",
                String::class.java, transactionId)

        //react
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(jsonPath("amount").value(transaction.amount))
        Assertions.assertThat(jsonPath("type").value(transaction.type))
        Assertions.assertThat(jsonPath("parent_id").doesNotExist())
    }

    @Test
    fun getTransaction_getTransactionDetailsAllFields() {
        //arrange
        val transactionId = TestHelper.randomTransactionId()
        val transaction = Transaction(transactionId, 100.1, "Test", 10)
        Persists.transactionMap.put(transactionId, transaction)
        //act
        val response = restTemplate.getForEntity("/transactionservice/transaction/{transactionId}",
                String::class.java, transactionId)

        //react
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(jsonPath("amount").value(transaction.amount))
        Assertions.assertThat(jsonPath("type").value(transaction.type))
        Assertions.assertThat(jsonPath("parent_id").value(transaction.parent_id!!))
    }

    @Test
    fun getTransactionIdsSameType_listOfTransactionIdsWithSameType() {
        //arrange
        var tranMap = HashMap<Long, String>()
        val type = "cars"
        tranMap.set(TestHelper.randomTransactionId(), type)
        tranMap.set(TestHelper.randomTransactionId(), type)

        for (tran in tranMap) {
            Persists.transactionMap.put(tran.key,
                    Transaction(tran.key, 100.1, tran.value, 10))
        }
        //act
        val response = restTemplate.getForEntity("/transactionservice/types/{types}", Array<Long>::class.java, type)

        //react
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(response.body).isEqualTo(tranMap.keys.toLongArray())

    }

    @Test
    fun getTransactionIdsSameType_singleTransactionIdDifferentTypes() {

        //arrange
        var tranMap = HashMap<String, Long>()
        tranMap.set("house", TestHelper.randomTransactionId())
        tranMap.set("car", TestHelper.randomTransactionId())

        for (tran in tranMap) {
            Persists.transactionMap.put(tran.value,
                    Transaction(tran.value, 100.1, tran.key, 10))
        }

        //act
        for (tran in tranMap) {
            val response = restTemplate.getForEntity("/transactionservice/types/{types}",
                    Array<Long>::class.java, tran.key)

            //react
            Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            Assertions.assertThat(response.body).isEqualTo(arrayOf(tran.value))
        }
    }

    @Test
    fun getSum_sumEqualsToAmountWhenNoParentId() {

        //arrange
        var tranMap = HashMap<Long, SumRequest>()
        tranMap.set(TestHelper.randomTransactionId(), SumRequest(100.01))
        tranMap.set(TestHelper.randomTransactionId(), SumRequest(-214.01))

        for (tran in tranMap) {
            Persists.transactionMap.put(tran.key,
                    Transaction(tran.key, tran.value.sum, "Test", null))
        }

        for (tran in tranMap) {
            //act
            val response = restTemplate.getForEntity("/transactionservice/sum/{transaction_id}", String::class.java,
                    tran.key)
            //response
            Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            Assertions.assertThat(response.body).isEqualTo(TestHelper.convertToJson(tran.value))
        }
    }

    @Test
    fun getSum_sumOfTransitiveTransactionsParentId() {

        //arrange
        var tranMapParent = HashMap<Long, Transaction>()
        var tranMapChild = HashMap<Long, Transaction>()
        (0..1).forEach {
            val transactionId: Long = it.toLong()
            tranMapParent.set(transactionId, Transaction(transactionId, 100.0, "Parent", null))
        }
        Persists.transactionMap.putAll(tranMapParent)
        (2..3).forEach {
            val transactionId: Long = it.toLong()
            tranMapChild.set(transactionId, Transaction(transactionId, it * 100.0, "Child0", 0))
        }
        Persists.transactionMap.putAll(tranMapChild)
        val total: Double = tranMapParent[0]!!.amount + tranMapChild[2]!!.amount + tranMapChild[3]!!.amount
        //act
        val response = restTemplate.getForEntity("/transactionservice/sum/{transaction_id}", String::class.java,
                0)
        //response
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(response.body).isEqualTo(TestHelper.convertToJson(SumRequest(total)))
    }

}