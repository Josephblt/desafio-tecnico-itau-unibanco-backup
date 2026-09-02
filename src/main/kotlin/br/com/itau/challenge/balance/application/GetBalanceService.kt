package br.com.itau.challenge.balance.application

import br.com.itau.challenge.balance.domain.model.AccountBalance
import br.com.itau.challenge.balance.port.input.GetBalanceUseCase
import br.com.itau.challenge.balance.port.output.BalanceProvider
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetBalanceService(
    private val balanceProvider: BalanceProvider,
) : GetBalanceUseCase {
    override fun getBalance(accountId: UUID): AccountBalance? = balanceProvider.findByAccountId(accountId)
}
