package com.n26.transaction

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TransactionWorkflowSpec extends TestBase {
    @Shared
    def parentTransaction = [:]

    @Shared
    def childTransaction = [:]

    @Shared
    def response = [:]

    def "Create a parent transaction"() {
        given: "customer wants to create a transaction"
        parentTransaction.id = 10
        parentTransaction.amount = 5000
        parentTransaction.type = "cars"

        when:
        "putTransaction endpoint is called for transaction ${parentTransaction.id}"
        response = testBuilder.transactionService {
            putTransaction {
                transactionId = parentTransaction.id
                body.amount = parentTransaction.amount
                body.type = parentTransaction.type
            }
        }
        then: "a new transaction is created"
        assert response.statusCode == 200

        when:
        "getTransaction endpoint is called for transaction ${parentTransaction.id}"
        response = testBuilder.transactionService {
            getTransaction {
                transactionId = parentTransaction.id
            }
        }
        then:
        "amount,type,parent_id is returned for transaction ${parentTransaction.id}"
        assert response.statusCode == 200
        assert response.body.amount == parentTransaction.amount
        assert response.body.type == parentTransaction.type
        assert response.body.parent_id == null

    }

    def "Create a child transaction"() {
        given: "customer wants to create a child transaction"
        childTransaction.id = 11
        childTransaction.amount = 10000
        childTransaction.type = "shopping"
        childTransaction.parent_id = parentTransaction.id

        when:
        "putTransaction endpoint is called for transaction ${childTransaction.id}"
        response = testBuilder.transactionService {
            putTransaction {
                transactionId = childTransaction.id
                body.amount = childTransaction.amount
                body.type = childTransaction.type
                body.parent_id = childTransaction.parent_id
            }
        }
        then: "a new transaction is created"
        assert response.statusCode == 200

        when:
        "getTransaction endpoint is called for transaction ${childTransaction.id}"
        response = testBuilder.transactionService {
            getTransaction {
                transactionId = childTransaction.id
            }
        }
        then:
        "amount,type,parent_id is returned for transaction ${parentTransaction.id}"
        assert response.statusCode == 200
        assert response.body.amount == childTransaction.amount
        assert response.body.type == childTransaction.type
        assert response.body.parent_id == childTransaction.parent_id
    }

    def "Get Transaction id lists based on type"() {
        when:
        "getType endpoint is called for type: ${parentTransaction.type}"
        response = testBuilder.transactionService {
            getType {
                type = parentTransaction.type
            }
        }
        then:
        "list of transaction is returned for type: ${parentTransaction.type}"
        assert response.statusCode == 200
        assert response.body == [parentTransaction.id]


        when:
        "getType endpoint is called for type: ${childTransaction.type}"
        response = testBuilder.transactionService {
            getType {
                type = childTransaction.type
            }
        }
        then:
        "list of transaction is returned for type: ${childTransaction.type}"
        assert response.statusCode == 200
        assert response.body == [childTransaction.id]
    }

    def "Get Sum of transactions which are transitively linked"() {
        when:
        "getSum endpoint is called for transaction ${parentTransaction.id}"
        response = testBuilder.transactionService {
            getSum {
                transactionId = parentTransaction.id
            }
        }
        then:
        "sum of amount from transaction ${parentTransaction} & ${childTransaction} is returned"
        assert response.statusCode == 200
        assert response.body.sum == parentTransaction.amount + childTransaction.amount

        when:
        "getSum endpoint is called for transaction ${childTransaction.id}"
        response = testBuilder.transactionService {
            getSum {
                transactionId = childTransaction.id
            }
        }
        then:
        "only the amount from transaction ${childTransaction} is returned"
        assert response.statusCode == 200
        assert response.body.sum == childTransaction.amount
    }
}
