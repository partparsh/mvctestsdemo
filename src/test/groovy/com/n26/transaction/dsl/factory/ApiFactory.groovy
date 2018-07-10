package com.n26.transaction.dsl.factory

class ApiFactory {
    def static factory
    def static getApi(def service) {
        def api = factory.api.newInstance()
        api.baseUrl = service.BASE_URL
        api.basePath = service.basePath
        return api
    }
    def static getApiWithBody(def service) {
        def api = getApi(service)
        api.body = factory.body.newInstance()
        return api
    }
}
