package br.com.itau.challenge.balance.port.output

import br.com.itau.challenge.balance.domain.model.AccountBalance
import br.com.itau.challenge.balance.domain.model.BalanceUpdateResult

fun interface BalanceRepository {
    fun saveIfNewer(balance: AccountBalance): BalanceUpdateResult
}
