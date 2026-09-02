package br.com.itau.challenge.balance.port.input

import br.com.itau.challenge.balance.domain.model.BalanceUpdateResult
import br.com.itau.challenge.balance.domain.model.FinancialTransactionEvent

fun interface ProcessFinancialTransactionUseCase {
    fun process(event: FinancialTransactionEvent): BalanceUpdateResult?
}
