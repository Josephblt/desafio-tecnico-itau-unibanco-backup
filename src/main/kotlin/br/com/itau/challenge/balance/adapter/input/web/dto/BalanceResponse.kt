package br.com.itau.challenge.balance.adapter.input.web.dto

import br.com.itau.challenge.balance.domain.model.AccountBalance
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

data class BalanceResponse(
    val id: UUID,
    val owner: UUID,
    val balance: BalancePayload,
    val updated_at: String,
) {
    companion object {
        private val saoPauloZone = ZoneId.of("America/Sao_Paulo")

        fun fromDomain(accountBalance: AccountBalance): BalanceResponse =
            BalanceResponse(
                id = accountBalance.id,
                owner = accountBalance.owner,
                balance =
                    BalancePayload(
                        amount = accountBalance.balance.amount,
                        currency = accountBalance.balance.currency,
                    ),
                updated_at = accountBalance.updatedAtMicros.toIsoOffsetDateTime(),
            )

        private fun Long.toIsoOffsetDateTime(): String {
            val instant = Instant.ofEpochSecond(this / 1_000_000, (this % 1_000_000) * 1_000)
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(instant.atZone(saoPauloZone))
        }
    }
}

data class BalancePayload(
    val amount: BigDecimal,
    val currency: String,
)
