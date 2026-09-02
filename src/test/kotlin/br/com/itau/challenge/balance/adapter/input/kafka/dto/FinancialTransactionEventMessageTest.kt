package br.com.itau.challenge.balance.adapter.input.kafka.dto

import br.com.itau.challenge.balance.BalanceFixtures
import br.com.itau.challenge.balance.domain.model.AccountStatus
import br.com.itau.challenge.balance.domain.model.TransactionStatus
import br.com.itau.challenge.balance.domain.model.TransactionType
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class FinancialTransactionEventMessageTest {
    @Test
    fun `maps Kafka message DTO to domain event`() {
        val message =
            FinancialTransactionEventMessage(
                transaction =
                    TransactionMessage(
                        id = BalanceFixtures.transactionId,
                        type = TransactionType.CREDIT,
                        amount = BigDecimal("97.07"),
                        currency = "BRL",
                        status = TransactionStatus.APPROVED,
                        timestamp = 1_751_749_453_433_000,
                    ),
                account =
                    AccountMessage(
                        id = BalanceFixtures.accountId,
                        owner = BalanceFixtures.ownerId,
                        created_at = 1_634_874_339_000_000,
                        status = AccountStatus.ENABLED,
                        balance = BalanceMessage(amount = BigDecimal("183.12"), currency = "BRL"),
                    ),
            )

        assertEquals(BalanceFixtures.event(), message.toDomain())
    }
}
