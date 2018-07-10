package com.n26.transaction.dsl.api

trait Api {
    String baseUrl
    String basePath
    def responseMap = [:]

    abstract exec()
}