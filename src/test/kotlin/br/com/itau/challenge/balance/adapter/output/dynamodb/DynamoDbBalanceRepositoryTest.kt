package br.com.itau.challenge.balance.adapter.output.dynamodb

import br.com.itau.challenge.balance.BalanceFixtures
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse
import kotlin.test.assertEquals

class DynamoDbBalanceRepositoryTest {
    @Test
    fun `conditional update only accepts new accounts newer timestamps or same transaction id`() {
        val client = mock(DynamoDbClient::class.java)
        given(client.updateItem(any(UpdateItemRequest::class.java))).willReturn(UpdateItemResponse.builder().build())
        val repository = DynamoDbBalanceRepository(client, "AccountBalances")

        repository.saveIfNewer(BalanceFixtures.balance())

        val requestCaptor = ArgumentCaptor.forClass(UpdateItemRequest::class.java)
        verify(client).updateItem(requestCaptor.capture())
        val request = requestCaptor.value
        assertEquals("AccountBalances", request.tableName())
        assertEquals(BalanceFixtures.accountId.toString(), request.key()["id"]?.s())
        assertEquals(
            "attribute_not_exists(#id) OR #updatedAtMicros < :updatedAtMicros OR #lastTransactionId = :lastTransactionId",
            request.conditionExpression(),
        )
        assertEquals("183.12", request.expressionAttributeValues()[":amount"]?.n())
        assertEquals("BRL", request.expressionAttributeValues()[":currency"]?.s())
    }

    @Test
    fun `returns ignored outdated when conditional update rejects event`() {
        val client = mock(DynamoDbClient::class.java)
        given(client.updateItem(any(UpdateItemRequest::class.java))).willThrow(ConditionalCheckFailedException.builder().build())
        val repository = DynamoDbBalanceRepository(client, "AccountBalances")

        val result = repository.saveIfNewer(BalanceFixtures.balance())

        assertEquals(br.com.itau.challenge.balance.domain.model.BalanceUpdateResult.IGNORED_OUTDATED, result)
    }

    @Test
    fun `maps DynamoDB item to domain balance`() {
        val client = mock(DynamoDbClient::class.java)
        given(client.getItem(any(GetItemRequest::class.java))).willReturn(
            GetItemResponse
                .builder()
                .item(
                    mapOf(
                        "id" to stringAttribute(BalanceFixtures.accountId.toString()),
                        "owner" to stringAttribute(BalanceFixtures.ownerId.toString()),
                        "balanceAmount" to numberAttribute("183.12"),
                        "balanceCurrency" to stringAttribute("BRL"),
                        "updatedAtMicros" to numberAttribute("1751749453433000"),
                        "lastTransactionId" to stringAttribute(BalanceFixtures.transactionId.toString()),
                    ),
                ).build(),
        )
        val repository = DynamoDbBalanceRepository(client, "AccountBalances")

        val balance = repository.findByAccountId(BalanceFixtures.accountId)

        assertEquals(BalanceFixtures.balance(), balance)
    }

    @Test
    fun `returns null when account is not present`() {
        val client = mock(DynamoDbClient::class.java)
        given(client.getItem(any(GetItemRequest::class.java))).willReturn(GetItemResponse.builder().build())
        val repository = DynamoDbBalanceRepository(client, "AccountBalances")

        val balance = repository.findByAccountId(BalanceFixtures.accountId)

        assertEquals(null, balance)
    }

    private fun stringAttribute(value: String) =
        software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder().s(value).build()

    private fun numberAttribute(value: String) =
        software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder().n(value).build()
}
