package com.n26.transaction

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TransactionApplication

fun main(args: Array<String>) {
    SpringApplication.run(TransactionApplication::class.java, *args)
}
