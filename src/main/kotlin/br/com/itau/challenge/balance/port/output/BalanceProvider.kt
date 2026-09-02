package br.com.itau.challenge.balance.port.output

import br.com.itau.challenge.balance.domain.model.AccountBalance
import java.util.UUID

fun interface BalanceProvider {
    fun findByAccountId(accountId: UUID): AccountBalance?
}
