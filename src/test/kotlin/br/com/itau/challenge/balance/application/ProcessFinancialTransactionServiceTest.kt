package br.com.itau.challenge.balance.application

import br.com.itau.challenge.balance.BalanceFixtures
import br.com.itau.challenge.balance.domain.model.AccountBalance
import br.com.itau.challenge.balance.domain.model.BalanceUpdateResult
import br.com.itau.challenge.balance.domain.model.TransactionStatus
import br.com.itau.challenge.balance.port.output.BalanceRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProcessFinancialTransactionServiceTest {
    @Test
    fun `valid event is persisted as latest balance snapshot`() {
        var savedBalance: AccountBalance? = null
        val repository =
            BalanceRepository {
                savedBalance = it
                BalanceUpdateResult.UPDATED
            }

        val result = ProcessFinancialTransactionService(repository).process(BalanceFixtures.event())

        assertEquals(BalanceUpdateResult.UPDATED, result)
        assertEquals(BalanceFixtures.balance(), savedBalance)
    }

    @Test
    fun `non-applicable event is ignored before reaching repository`() {
        var calls = 0
        val repository =
            BalanceRepository {
                calls++
                BalanceUpdateResult.UPDATED
            }

        val result = ProcessFinancialTransactionService(repository).process(BalanceFixtures.event(status = TransactionStatus.REJECTED))

        assertNull(result)
        assertEquals(0, calls)
    }
}
