package com.n26.transaction.request

data class TransactionRequest(val amount: Double, val type: String, val parent_id: Long?)