package br.com.itau.challenge.balance

import br.com.itau.challenge.balance.domain.model.AccountBalance
import br.com.itau.challenge.balance.domain.model.AccountStatus
import br.com.itau.challenge.balance.domain.model.FinancialAccount
import br.com.itau.challenge.balance.domain.model.FinancialTransaction
import br.com.itau.challenge.balance.domain.model.FinancialTransactionEvent
import br.com.itau.challenge.balance.domain.model.Money
import br.com.itau.challenge.balance.domain.model.TransactionStatus
import br.com.itau.challenge.balance.domain.model.TransactionType
import java.math.BigDecimal
import java.util.UUID

object BalanceFixtures {
    val accountId: UUID = UUID.fromString("5b19c8b6-0cc4-4c72-a989-0c2ee15fa975")
    val ownerId: UUID = UUID.fromString("315e3cfe-f4af-4cd2-b298-a449e614349a")
    val transactionId: UUID = UUID.fromString("8e8ae808-b154-48b5-9f3e-553935cc4543")

    fun balance(
        updatedAtMicros: Long = 1_751_749_453_433_000,
        transactionId: UUID = BalanceFixtures.transactionId,
    ): AccountBalance =
        AccountBalance(
            id = accountId,
            owner = ownerId,
            balance = Money(amount = BigDecimal("183.12"), currency = "BRL"),
            updatedAtMicros = updatedAtMicros,
            lastTransactionId = transactionId,
        )

    fun event(
        status: TransactionStatus = TransactionStatus.APPROVED,
        accountStatus: AccountStatus = AccountStatus.ENABLED,
        timestampMicros: Long = 1_751_749_453_433_000,
        amount: BigDecimal = BigDecimal("97.07"),
        transactionCurrency: String = "BRL",
        balanceCurrency: String = "BRL",
    ): FinancialTransactionEvent =
        FinancialTransactionEvent(
            transaction =
                FinancialTransaction(
                    id = transactionId,
                    type = TransactionType.CREDIT,
                    amount = amount,
                    currency = transactionCurrency,
                    status = status,
                    timestampMicros = timestampMicros,
                ),
            account =
                FinancialAccount(
                    id = accountId,
                    owner = ownerId,
                    createdAtMicros = 1_634_874_339_000_000,
                    status = accountStatus,
                    balance = Money(amount = BigDecimal("183.12"), currency = balanceCurrency),
                ),
        )
}
