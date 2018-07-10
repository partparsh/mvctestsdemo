package com.n26.transaction

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.concurrent.ThreadLocalRandom

object TestHelper {
    val convertToJsonAndRemoveEntries = { it: Any, keys: Array<String>? ->
        val jsonNode = jacksonObjectMapper().convertValue(it, ObjectNode::class.java)
        keys?.forEach {
            jsonNode.remove(it)
        }
        jsonNode.toString()
    }

    val convertToJson = { it: Any ->
        val jsonString = jacksonObjectMapper().writeValueAsString(it)
        jsonString
    }

    val randomTransactionId = {
        val random: Long = ThreadLocalRandom.current().nextLong((100 + 1) - (0)) + (0)
        random
    }

    val randomAmount = {
        val random: Double = ThreadLocalRandom.current().nextDouble((100.00 + 1.00) - (-100.00)) + (-100.00)
        random
    }

}