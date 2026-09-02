package br.com.itau.challenge.balance.application

import br.com.itau.challenge.balance.domain.model.BalanceUpdateResult
import br.com.itau.challenge.balance.domain.model.FinancialTransactionEvent
import br.com.itau.challenge.balance.port.input.ProcessFinancialTransactionUseCase
import br.com.itau.challenge.balance.port.output.BalanceRepository
import org.springframework.stereotype.Service

@Service
class ProcessFinancialTransactionService(
    private val balanceRepository: BalanceRepository,
) : ProcessFinancialTransactionUseCase {
    override fun process(event: FinancialTransactionEvent): BalanceUpdateResult? {
        val snapshot = event.toBalanceSnapshotOrNull() ?: return null
        return balanceRepository.saveIfNewer(snapshot)
    }
}
