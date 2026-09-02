package br.com.itau.challenge.balance.domain.model

data class FinancialTransactionEvent(
    val transaction: FinancialTransaction,
    val account: FinancialAccount,
) {
    fun toBalanceSnapshotOrNull(): AccountBalance? {
        if (!transaction.isApproved()) return null
        if (account.status != AccountStatus.ENABLED) return null
        if (transaction.timestampMicros <= 0) return null
        if (!account.balance.isValid()) return null
        if (transaction.amount <= java.math.BigDecimal.ZERO) return null
        if (transaction.currency.isBlank()) return null

        return AccountBalance(
            id = account.id,
            owner = account.owner,
            balance = account.balance,
            updatedAtMicros = transaction.timestampMicros,
            lastTransactionId = transaction.id,
        )
    }
}
