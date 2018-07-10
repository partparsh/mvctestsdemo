package com.n26.transaction.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(TransactionOwnParentException::class)
    protected fun transactionOwnParentException(exception: TransactionOwnParentException, request: WebRequest): ResponseEntity<Any> {
        val bodyOfResponse = "Transaction cannot be its own parent"
        return handleExceptionInternal(exception, bodyOfResponse, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(TransactionNotFoundException::class)
    protected fun transactionNotFoundException(exception: TransactionNotFoundException, request: WebRequest): ResponseEntity<Any> {
        val bodyOfResponse = "Transaction not found"
        return handleExceptionInternal(exception, bodyOfResponse, HttpHeaders(), HttpStatus.NOT_FOUND, request)
    }

    @ExceptionHandler(TypeNotFoundException::class)
    protected fun typeNotFoundException(exception: TypeNotFoundException, request: WebRequest): ResponseEntity<Any> {
        val bodyOfResponse = "Type not found"
        return handleExceptionInternal(exception, bodyOfResponse, HttpHeaders(), HttpStatus.NOT_FOUND, request)
    }
}