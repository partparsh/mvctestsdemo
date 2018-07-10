package com.n26.transaction.dsl.api

import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder

trait GetSumPath {
    Long transactionId
}

class GetSumApi implements GetSumPath, Api {
    @Override
    def exec() {
        HttpBuilder.configure {
            request.uri = baseUrl
            request.setAccept("application/json")
            request.setContentType("application/json")
        }.get() {
            request.uri.path = basePath + "/" + transactionId
            response.success { FromServer fs, Object body ->
                responseMap.statusCode = fs.statusCode
                responseMap.body = body
            }
            response.failure { FromServer fs, Object body ->
                responseMap.statusCode = fs.statusCode
                responseMap.body = body
            }
        }
    }
}
