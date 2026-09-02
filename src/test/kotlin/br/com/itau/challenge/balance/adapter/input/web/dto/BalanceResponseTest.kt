package br.com.itau.challenge.balance.adapter.input.web.dto

import br.com.itau.challenge.balance.BalanceFixtures
import kotlin.test.Test
import kotlin.test.assertEquals

class BalanceResponseTest {
    @Test
    fun `maps domain balance to response contract`() {
        val response = BalanceResponse.fromDomain(BalanceFixtures.balance())

        assertEquals(BalanceFixtures.accountId, response.id)
        assertEquals(BalanceFixtures.ownerId, response.owner)
        assertEquals("183.12".toBigDecimal(), response.balance.amount)
        assertEquals("BRL", response.balance.currency)
        assertEquals("2025-07-05T18:04:13.433-03:00", response.updated_at)
    }
}
