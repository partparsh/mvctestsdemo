package com.paresh.mvctestsdemo.domain

data class Transaction(val transaction_id: Long, val amount: Double, val type: String, val parent_id: Long?)