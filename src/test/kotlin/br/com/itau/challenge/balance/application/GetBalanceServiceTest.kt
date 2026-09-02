package br.com.itau.challenge.balance.application

import br.com.itau.challenge.balance.BalanceFixtures
import br.com.itau.challenge.balance.port.output.BalanceProvider
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetBalanceServiceTest {
    @Test
    fun `returns balance from provider`() {
        val provider = BalanceProvider { BalanceFixtures.balance() }

        val result = GetBalanceService(provider).getBalance(BalanceFixtures.accountId)

        assertEquals(BalanceFixtures.balance(), result)
    }

    @Test
    fun `returns null when provider does not find account`() {
        val provider = BalanceProvider { null }

        val result = GetBalanceService(provider).getBalance(UUID.randomUUID())

        assertNull(result)
    }
}
