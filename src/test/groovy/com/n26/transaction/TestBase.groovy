package com.n26.transaction

import com.n26.transaction.dsl.E2E
import com.n26.transaction.repository.Persists
import spock.lang.Shared
import spock.lang.Specification


class TestBase extends Specification {
    @Shared
    def testBuilder = E2E.instance

    def setupSpec() {
        Persists.transactionMap.clear()
    }

    def cleaupSpec() {
        Persists.transactionMap.clear()
    }
}
