package br.com.itau.challenge.balance.adapter.input.kafka.dto

import br.com.itau.challenge.balance.domain.model.AccountStatus
import br.com.itau.challenge.balance.domain.model.FinancialAccount
import br.com.itau.challenge.balance.domain.model.FinancialTransaction
import br.com.itau.challenge.balance.domain.model.FinancialTransactionEvent
import br.com.itau.challenge.balance.domain.model.Money
import br.com.itau.challenge.balance.domain.model.TransactionStatus
import br.com.itau.challenge.balance.domain.model.TransactionType
import java.math.BigDecimal
import java.util.UUID

data class FinancialTransactionEventMessage(
    val transaction: TransactionMessage,
    val account: AccountMessage,
) {
    fun toDomain(): FinancialTransactionEvent =
        FinancialTransactionEvent(
            transaction =
                FinancialTransaction(
                    id = transaction.id,
                    type = transaction.type,
                    amount = transaction.amount,
                    currency = transaction.currency,
                    status = transaction.status,
                    timestampMicros = transaction.timestamp,
                ),
            account =
                FinancialAccount(
                    id = account.id,
                    owner = account.owner,
                    createdAtMicros = account.created_at,
                    status = account.status,
                    balance = Money(amount = account.balance.amount, currency = account.balance.currency),
                ),
        )
}

data class TransactionMessage(
    val id: UUID,
    val type: TransactionType,
    val amount: BigDecimal,
    val currency: String,
    val status: TransactionStatus,
    val timestamp: Long,
)

data class AccountMessage(
    val id: UUID,
    val owner: UUID,
    val created_at: Long,
    val status: AccountStatus,
    val balance: BalanceMessage,
)

data class BalanceMessage(
    val amount: BigDecimal,
    val currency: String,
)
