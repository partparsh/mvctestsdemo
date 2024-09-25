package com.paresh.mvctestsdemo.dsl

import org.junit.jupiter.api.*
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TransactionWorkflowTests {

    private var parentTransactionId: Long = 100
    private var parentTransaction = TransactionRequest(10000.0, "house")
    private var childTransactionId: Long = 110
    private var childTransaction = TransactionRequest(100.0, "insurance", parentTransactionId)

    @Test
    @Order(1)
    fun createParentTransaction() {
        apiTest {
           createTransaction {
                transaction_id = parentTransactionId
                amount = parentTransaction.amount
                type = parentTransaction.type
            }
            verifyResponse {
                statusCode isEqual 200
            }
            getTransaction {
                transaction_id = parentTransactionId
            }
            verifyResponse {
                statusCode isEqual 200
                body isEqual parentTransaction
            }
        }
    }

    @Test
    @Order(2)
    fun createChildTransaction() {
        apiTest {
            createTransaction {
                transaction_id = childTransactionId
                amount = childTransaction.amount
                type = childTransaction.type
                parent_id = childTransaction.parent_id
            }
            verifyResponse {
                statusCode isEqual 200
            }
            getTransaction {
                transaction_id = childTransactionId
            }
            verifyResponse {
                statusCode isEqual 200
                body isEqual childTransaction
            }
        }
    }

    @Test
    @Order(3)
    fun getTransactionIDBasedOnType() {
        val expectedBody = arrayOf(parentTransactionId)
        apiTest {
            getType {
                type = parentTransaction.type
            }
            verifyResponse {
                statusCode isEqual 200
                body isEqual expectedBody
            }
        }
    }

    @Test
    @Order(4)
    fun getSumOfTransactionsTransitivelyLinked() {
        val parentSum = SumRequest(sum = (parentTransaction.amount?.plus(childTransaction.amount!!)))
        val childSum = SumRequest(sum = childTransaction.amount)
        apiTest {
            getSum {
                transaction_id = parentTransactionId
            }
            verifyResponse {
                statusCode isEqual 200
                body isEqual parentSum
            }
            getSum {
                transaction_id = childTransactionId
            }
            verifyResponse {
                statusCode isEqual 200
                body isEqual childSum
            }
        }
    }
}

