package com.n26.transaction.dsl

import com.n26.transaction.dsl.factory.ServiceFactory
import com.n26.transaction.dsl.service.Transaction

@Singleton
class E2E {
    private static transactionServiceFactory = [service: Transaction]

    static transactionService (@DelegatesTo(Transaction.class)Closure transactionClosure) {
        ServiceFactory.factory = transactionServiceFactory
        transactionClosure.setDelegate(ServiceFactory.getService())
        transactionClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
        return transactionClosure.call()
    }


}
