package br.com.itau.challenge.balance.adapter.input.kafka

import br.com.itau.challenge.balance.domain.model.BalanceUpdateResult
import br.com.itau.challenge.balance.domain.model.FinancialTransactionEvent
import br.com.itau.challenge.balance.port.input.ProcessFinancialTransactionUseCase
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import kotlin.test.Test
import kotlin.test.assertEquals

class FinancialTransactionConsumerTest {
    private val objectMapper =
        JsonMapper
            .builder()
            .addModule(KotlinModule.Builder().build())
            .build()

    @Test
    fun `deserializes JSON payload and delegates valid event to use case`() {
        var receivedEvent: FinancialTransactionEvent? = null
        val useCase =
            ProcessFinancialTransactionUseCase {
                receivedEvent = it
                BalanceUpdateResult.UPDATED
            }
        val consumer = FinancialTransactionConsumer(useCase, objectMapper)

        consumer.consume(
            """
            {
              "transaction": {
                "id": "8e8ae808-b154-48b5-9f3e-553935cc4543",
                "type": "CREDIT",
                "amount": 97.07,
                "currency": "BRL",
                "status": "APPROVED",
                "timestamp": 1751749453433000
              },
              "account": {
                "id": "5b19c8b6-0cc4-4c72-a989-0c2ee15fa975",
                "owner": "315e3cfe-f4af-4cd2-b298-a449e614349a",
                "created_at": 1634874339000000,
                "status": "ENABLED",
                "balance": {
                  "amount": 183.12,
                  "currency": "BRL"
                }
              }
            }
            """.trimIndent(),
        )

        assertEquals("5b19c8b6-0cc4-4c72-a989-0c2ee15fa975", receivedEvent?.account?.id.toString())
    }

    @Test
    fun `ignores malformed JSON payload without delegating`() {
        var calls = 0
        val useCase =
            ProcessFinancialTransactionUseCase {
                calls++
                BalanceUpdateResult.UPDATED
            }
        val consumer = FinancialTransactionConsumer(useCase, objectMapper)

        consumer.consume("""{"transaction":""")

        assertEquals(0, calls)
    }

    @Test
    fun `handles ignored outdated result`() {
        val consumer =
            FinancialTransactionConsumer(
                ProcessFinancialTransactionUseCase { BalanceUpdateResult.IGNORED_OUTDATED },
                objectMapper,
            )

        consumer.consume(validPayload())
    }

    @Test
    fun `handles non-applicable result`() {
        val consumer =
            FinancialTransactionConsumer(
                ProcessFinancialTransactionUseCase { null },
                objectMapper,
            )

        consumer.consume(validPayload())
    }

    private fun validPayload(): String =
        """
        {
          "transaction": {
            "id": "8e8ae808-b154-48b5-9f3e-553935cc4543",
            "type": "CREDIT",
            "amount": 97.07,
            "currency": "BRL",
            "status": "APPROVED",
            "timestamp": 1751749453433000
          },
          "account": {
            "id": "5b19c8b6-0cc4-4c72-a989-0c2ee15fa975",
            "owner": "315e3cfe-f4af-4cd2-b298-a449e614349a",
            "created_at": 1634874339000000,
            "status": "ENABLED",
            "balance": {"amount": 183.12, "currency": "BRL"}
          }
        }
        """.trimIndent()
}
