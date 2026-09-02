package br.com.itau.challenge.balance.domain.model

import java.math.BigDecimal

data class Money(
    val amount: BigDecimal,
    val currency: String,
) {
    fun isValid(): Boolean = currency.matches(ISO_4217_PATTERN)

    private companion object {
        val ISO_4217_PATTERN = Regex("[A-Z]{3}")
    }
}
