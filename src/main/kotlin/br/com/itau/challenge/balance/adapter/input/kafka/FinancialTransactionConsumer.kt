package br.com.itau.challenge.balance.adapter.input.kafka

import br.com.itau.challenge.balance.adapter.input.kafka.dto.FinancialTransactionEventMessage
import br.com.itau.challenge.balance.domain.model.BalanceUpdateResult
import br.com.itau.challenge.balance.port.input.ProcessFinancialTransactionUseCase
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class FinancialTransactionConsumer(
    private val processFinancialTransactionUseCase: ProcessFinancialTransactionUseCase,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["\${financial-transactions.topic-name}"])
    fun consume(payload: String) {
        val event =
            runCatching {
                objectMapper
                    .readValue(payload, FinancialTransactionEventMessage::class.java)
                    .toDomain()
            }.getOrElse {
                logger.warn("Ignoring invalid financial transaction event: {}", it.message)
                return
            }

        when (processFinancialTransactionUseCase.process(event)) {
            BalanceUpdateResult.UPDATED -> logger.info("Balance updated for account {}", event.account.id)
            BalanceUpdateResult.IGNORED_OUTDATED -> logger.info("Ignored outdated balance event for account {}", event.account.id)
            null -> logger.info("Ignored non-applicable balance event for account {}", event.account.id)
        }
    }
}
