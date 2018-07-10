package com.n26.transaction.dsl.factory


class ServiceFactory {
    def static factory
    def static getService(){
        return factory.service.newInstance()
    }
}
