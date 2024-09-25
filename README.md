# Transaction Service 

Demonstration of Spring MVC testing methodologies and Kotlin DSL creating to test APIs

## Assumption:

- A transaction cannot be its own parent. Following transaction will not be accepted:
```
transaction_id:10
{"amount":10.0, "type":"Not Accepted", "parent_id":10}
```

- Java version:
```Bash
~ java --version
openjdk 17.0.12 2024-07-16 LTS
```

## Implementation:
- TDD was used to implement tests (unit / Integration) first and then service. Top-Down approach was used.
- Integration test (Happy path) was written first for one endpoint.
- Unit test (Happy path + Edge Cases+ Positive flow + Negative Flow) for each Spring Web MVC components were written and
  implemented.
- Test Repository also contains a DSL implementing End2End workflow developed in Kotlin,
- This DSL gives the power to write tests in more readable way.

To run the tests cd into the directory and do following in terminal:

```
./gradlew clean test

```
Flow of Tests + Service implementation:

1. Write integration test (happy path) for particular endpoint and it should fail.
2. Write controller unit test (Happy path) and it will fail.
3. Minimum implementation of the controller to make 2. pass
4. Write controller unit tests (edge cases) and it will fail.
5. Minimum implementation of the controller to make 4. pass
6. Do the same for Service layer, Repository layer until Integration tests passes.

### Notes:
- Repository tests have not been implemented as no orm is being used and most of the Repository methods are simply leveraging
  kotlin's map collection.

### Using kotlin´s DSL capabilities:

```Kotlin
fun apiTest(init: ApiTestExecutor.() -> Unit) {
    ApiTestExecutor().init()
}

apiTest { 
    
}
```
In the above code, as lambda is the last parameter to the function, `()` can be omitted.

function definition, `fun apiTest(init: ApiTestExecutor.() -> Unit)`, the `ApiTestExecutor.() -> unit` tells compiler that
the lambda should be executed in the context of the object of `ApiTestExecutor` (i.e. the lambda will execute like it is 
executing inside the object, it can then access object properties and methods). This enables autocompletion on IDE.

```Kotlin

data class Transaction( var transaction_id: Long = 0, var amount: Double? = 0.0, var type: String? = "", var parent_id: Long?= null)
data class TransactionRequest(var amount: Double? = 0.0, var type: String? = "", var parent_id: Long?= null)

fun apiTest(block: ApiTestExecutor.() -> Unit) {
  ApiTestExecutor().apply(block)
}

class ApiTestExecutor {
    private val URL = "http://localhost:8080"
    private var client: RestTemplate = RestTemplate()
    private var response: ResponseEntity<*>? = null

    fun createTransaction(block: Transaction.() -> Unit) {
        val transaction: Transaction = Transaction().apply(block)
        val transactionRequest = TransactionRequest(transaction.amount, transaction.type, transaction.parent_id)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(transactionRequest, headers)
        response = client.exchange(
            "${URL}/transactionservice/transaction/${transaction.transaction_id}",
            HttpMethod.PUT,
            entity,
            Void::class.java
        )
    }
}

apiTest {
    createTransaction {
      transaction_id = 1
      amount = 10.0
      type = "chocolate"
    }
}

```
`createTransaction` method is now accessible inside `apiTest` block. This `lock` is the lambda `ApiTestExecutor().apply(block)`.



`fun createTransaction(block: Transaction.() -> Unit)` is telling compiler that for method `createTransaction` the last parameter is lambda
which will run in context of data class object `Transaction` and can access `Transaction` object´s properties like `transaction_id`, `amount`
We use this object to call rest endpoint to create transaction and store the response 

```Kotlin

data class Transaction( var transaction_id: Long = 0, var amount: Double? = 0.0, var type: String? = "", var parent_id: Long?= null)
data class TransactionRequest(var amount: Double? = 0.0, var type: String? = "", var parent_id: Long?= null)

fun apiTest(block: ApiTestExecutor.() -> Unit) {
  ApiTestExecutor().apply(block)
}

class ApiTestExecutor {
    private val URL = "http://localhost:8080"
    private var client: RestTemplate = RestTemplate()
    private var response: ResponseEntity<*>? = null

    fun createTransaction(block: Transaction.() -> Unit) {
        val transaction: Transaction = Transaction().apply(block)
        val transactionRequest = TransactionRequest(transaction.amount, transaction.type, transaction.parent_id)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(transactionRequest, headers)
        response = client.exchange(
            "${URL}/transactionservice/transaction/${transaction.transaction_id}",
            HttpMethod.PUT,
            entity,
            Void::class.java
        )
    }
  fun verifyResponse(block: ResponseVerification.() -> Unit) {
    ResponseVerification(response).apply(block)
  }
}

data object StatusCode {
  const val PRINT_MESSAGE = "Status code"
}
data object Body {
  const val PRINT_MESSAGE = "Body"
}

class ResponseVerification(private val response: ResponseEntity<*>?) {
    var statusCode = StatusCode

    infix fun StatusCode.isEqual(expected: Int?) {
        if (response?.statusCode?.value() == expected) {
            println("${this.PRINT_MESSAGE} verification passed: ${response?.statusCode?.value()} == $expected")
        } else {
            throw AssertionError("${this.PRINT_MESSAGE} verification failed: ${response?.statusCode?.value()} != $expected")
        }
    }
}

apiTest {
    createTransaction {
      transaction_id = 1
      amount = 10.0
      type = "chocolate"
    }
  verifyResponse {
    statusCode isEqual 200
  }
}

```

function `verifyResponse` runs the lambda under the context of `ResponseVerification`. Which enables us to use `statusCode`
object which has a contextual method `infix fun StatusCode.isEqual(expected: Int?)` to assert statusCodes against the response.
The `infix` keyword enables us to remove the `()`. Without `infix`. The function call would look like this: 

```Kotlin
  verifyResponse {
    statusCode isEqual(200)
  }
```