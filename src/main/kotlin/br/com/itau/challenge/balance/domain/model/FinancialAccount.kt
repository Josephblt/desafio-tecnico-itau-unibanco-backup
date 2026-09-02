package br.com.itau.challenge.balance.domain.model

import java.util.UUID

data class FinancialAccount(
    val id: UUID,
    val owner: UUID,
    val createdAtMicros: Long,
    val status: AccountStatus,
    val balance: Money,
)
