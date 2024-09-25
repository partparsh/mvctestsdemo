package com.paresh.mvctestsdemo.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ExceptionControllerAdvice {

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Transaction not found")
    @ExceptionHandler(TransactionNotFoundException::class)
    fun transactionNotFoundException(exception: TransactionNotFoundException){}

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Type not found")
    @ExceptionHandler(TypeNotFoundException::class)
    fun typeNotFoundException(exception: TypeNotFoundException){}

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Transaction cannot be its own parent")
    @ExceptionHandler(TransactionOwnParentException::class)
    fun transactionOwnParentException(exception: TransactionOwnParentException){}

}