package br.com.itau.challenge.balance.domain.model

import java.math.BigDecimal
import java.util.UUID

data class FinancialTransaction(
    val id: UUID,
    val type: TransactionType,
    val amount: BigDecimal,
    val currency: String,
    val status: TransactionStatus,
    val timestampMicros: Long,
) {
    fun isApproved(): Boolean = status == TransactionStatus.APPROVED
}
