package com.n26.transaction.dsl.api

import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder

trait GetTypePath {
    String type
}
class GetTypeApi implements GetTypePath, Api{
    @Override
    def exec() {
        HttpBuilder.configure {
            request.uri = baseUrl
            request.setAccept("application/json")
            request.setContentType("application/json")
        }.get() {
            request.uri.path = basePath + "/" + type
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
