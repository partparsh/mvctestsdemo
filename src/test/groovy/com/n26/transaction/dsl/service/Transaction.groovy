package com.n26.transaction.dsl.service

import com.n26.transaction.dsl.api.GetSumApi
import com.n26.transaction.dsl.api.GetTransactionApi
import com.n26.transaction.dsl.api.GetTypeApi
import com.n26.transaction.dsl.api.PutTransactionApi
import com.n26.transaction.dsl.body.PutTransactionBody
import com.n26.transaction.dsl.factory.ApiFactory


class Transaction implements PutTransaction, GetTransaction, GetType, GetSum {
}

trait PutTransaction extends Service {

    private def putTransactionApiFactory = [api: PutTransactionApi, body: PutTransactionBody]

    def putTransaction(@DelegatesTo(PutTransactionApi.class) Closure putTransactionClosure){
        basePath = "/transactionservice/transaction"
        ApiFactory.factory = putTransactionApiFactory
        putTransactionClosure.setDelegate(ApiFactory.getApiWithBody(this))
        putTransactionClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        putTransactionClosure.call()
        putTransactionClosure.exec()
        return putTransactionClosure.responseMap

    }
}

trait GetTransaction extends Service {

    private def getTransactionApiFactory = [api: GetTransactionApi]

    def getTransaction(@DelegatesTo(GetTransactionApi.class) Closure getTransactionClosure){
        basePath = "/transactionservice/transaction"
        ApiFactory.factory = getTransactionApiFactory
        getTransactionClosure.setDelegate(ApiFactory.getApi(this))
        getTransactionClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        getTransactionClosure.call()
        getTransactionClosure.exec()
        return getTransactionClosure.responseMap

    }
}

trait GetType extends Service {

    private def getTypeApiFactory = [api: GetTypeApi]

    def getType(@DelegatesTo(GetTypeApi.class) Closure getTypeClosure){
        basePath = "/transactionservice/types"
        ApiFactory.factory = getTypeApiFactory
        getTypeClosure.setDelegate(ApiFactory.getApi(this))
        getTypeClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        getTypeClosure.call()
        getTypeClosure.exec()
        return getTypeClosure.responseMap
    }
}
trait GetSum extends Service {

    private def getSumApiFactory = [api: GetSumApi]

    def getSum(@DelegatesTo(GetSumApi.class) Closure getSumClosure){
        basePath = "/transactionservice/sum"
        ApiFactory.factory = getSumApiFactory
        getSumClosure.setDelegate(ApiFactory.getApi(this))
        getSumClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        getSumClosure.call()
        getSumClosure.exec()
        return getSumClosure.responseMap
    }
}

