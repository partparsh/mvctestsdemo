package com.paresh.mvctestsdemo.dsl

import org.springframework.http.ResponseEntity

data object StatusCode {
    const val PRINT_MESSAGE = "Status code"
}
data object Body {
    const val PRINT_MESSAGE = "Body"
}

class ResponseVerification(private val response: ResponseEntity<*>?) {
    var statusCode = StatusCode
    var body = Body

    infix fun StatusCode.isEqual(expected: Int?) {
        if (response?.statusCode?.value() == expected) {
            println("${this.PRINT_MESSAGE} verification passed: ${response?.statusCode?.value()} == $expected")
        } else {
            throw AssertionError("${this.PRINT_MESSAGE} verification failed: ${response?.statusCode?.value()} != $expected")
        }
    }
    infix fun <T : Any> Body.isEqual(expected: T) {
        if (expected == response?.body) {
            println("${this.PRINT_MESSAGE} verification passed: ${response.body} == $expected")
        } else {
            throw AssertionError("${this.PRINT_MESSAGE} verification failed: ${response?.body} != $expected \n Type information responseBody: ${response?.body!!::class.simpleName} expected: ${expected::class.simpleName}")
        }
    }
    infix fun <T> Body.isEqual(expected: Array<T>) {
        val responseBody = response?.body
        if (responseBody is Array<*> && responseBody.contentEquals(expected)) {
            println("${this.PRINT_MESSAGE} verification passed: ${responseBody.contentToString()} == ${expected.contentToString()}")
        } else {
            throw AssertionError("${this.PRINT_MESSAGE} verification failed: ${response?.body} != ${expected.contentToString()} \n Type information responseBody: ${responseBody!!::class.simpleName} expected: ${expected::class.simpleName}")
        }
    }
}