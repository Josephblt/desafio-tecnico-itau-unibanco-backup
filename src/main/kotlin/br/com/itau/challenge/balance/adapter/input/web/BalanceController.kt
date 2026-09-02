package br.com.itau.challenge.balance.adapter.input.web

import br.com.itau.challenge.balance.adapter.input.web.dto.BalanceResponse
import br.com.itau.challenge.balance.port.input.GetBalanceUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class BalanceController(
    private val getBalanceUseCase: GetBalanceUseCase,
) {
    @GetMapping("/balances/{accountId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBalance(
        @PathVariable accountId: String,
    ): ResponseEntity<BalanceResponse> {
        val accountUuid =
            runCatching { UUID.fromString(accountId) }.getOrElse {
                return ResponseEntity.badRequest().build()
            }

        val balance = getBalanceUseCase.getBalance(accountUuid) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        return ResponseEntity.ok(BalanceResponse.fromDomain(balance))
    }
}
