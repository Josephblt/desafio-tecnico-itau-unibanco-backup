package br.com.itau.challenge.balance.domain.model

import br.com.itau.challenge.balance.BalanceFixtures
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FinancialTransactionEventTest {
    @Test
    fun `approved transaction for enabled account becomes balance snapshot`() {
        val snapshot = BalanceFixtures.event().toBalanceSnapshotOrNull()

        assertEquals(BalanceFixtures.balance(), snapshot)
    }

    @Test
    fun `declined transaction is ignored`() {
        val snapshot = BalanceFixtures.event(status = TransactionStatus.DECLINED).toBalanceSnapshotOrNull()

        assertNull(snapshot)
    }

    @Test
    fun `disabled account is ignored`() {
        val snapshot = BalanceFixtures.event(accountStatus = AccountStatus.DISABLED).toBalanceSnapshotOrNull()

        assertNull(snapshot)
    }

    @Test
    fun `invalid monetary currency is ignored`() {
        val snapshot = BalanceFixtures.event(balanceCurrency = "REAL").toBalanceSnapshotOrNull()

        assertNull(snapshot)
    }

    @Test
    fun `non-positive transaction amount is ignored`() {
        val snapshot = BalanceFixtures.event(amount = BigDecimal.ZERO).toBalanceSnapshotOrNull()

        assertNull(snapshot)
    }

    @Test
    fun `non-positive timestamp is ignored`() {
        val snapshot = BalanceFixtures.event(timestampMicros = 0).toBalanceSnapshotOrNull()

        assertNull(snapshot)
    }
}
