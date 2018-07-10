package com.n26.transaction.dsl.api

import com.n26.transaction.dsl.body.PutTransactionBody
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder

trait PutTransactionPath {
    Long transactionId
}

class PutTransactionApi implements PutTransactionPath, Api {
    PutTransactionBody body

    @Override
    def exec() {
        HttpBuilder.configure {
            request.uri = baseUrl
            request.setContentType("application/json")
        }.put() {
            request.uri.path = basePath + "/" + transactionId
            request.body = body
            response.success { FromServer fs ->
                responseMap.statusCode = fs.statusCode
            }
            response.failure { FromServer fs ->
                responseMap.statusCode = fs.statusCode
            }
        }
    }
}
