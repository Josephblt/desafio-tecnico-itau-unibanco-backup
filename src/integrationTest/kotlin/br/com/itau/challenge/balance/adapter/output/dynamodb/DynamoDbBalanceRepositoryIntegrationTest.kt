package br.com.itau.challenge.balance.adapter.output.dynamodb

import br.com.itau.challenge.balance.domain.model.AccountBalance
import br.com.itau.challenge.balance.domain.model.BalanceUpdateResult
import br.com.itau.challenge.balance.domain.model.Money
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import java.math.BigDecimal
import java.net.URI
import java.util.UUID
import kotlin.test.assertEquals

class DynamoDbBalanceRepositoryIntegrationTest {
    private val tableName = System.getenv("BALANCE_TABLE_NAME") ?: "AccountBalances"
    private val accountId = UUID.randomUUID()
    private val ownerId = UUID.randomUUID()
    private val firstTransactionId = UUID.randomUUID()
    private val olderTransactionId = UUID.randomUUID()

    private val dynamoDbClient: DynamoDbClient =
        DynamoDbClient
            .builder()
            .endpointOverride(URI.create(System.getenv("DYNAMODB_ENDPOINT") ?: "http://localhost:8000"))
            .region(Region.of(System.getenv("DYNAMODB_REGION") ?: "us-east-1"))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
            .build()

    private val repository = DynamoDbBalanceRepository(dynamoDbClient, tableName)

    @AfterEach
    fun tearDown() {
        dynamoDbClient.deleteItem(
            DeleteItemRequest
                .builder()
                .tableName(tableName)
                .key(mapOf("id" to AttributeValue.builder().s(accountId.toString()).build()))
                .build(),
        )
    }

    @Test
    fun `conditional write keeps the newest balance snapshot`() {
        val firstBalance = balance(updatedAtMicros = 200, transactionId = firstTransactionId, amount = "100.00")
        val olderBalance = balance(updatedAtMicros = 100, transactionId = olderTransactionId, amount = "50.00")

        assertEquals(BalanceUpdateResult.UPDATED, repository.saveIfNewer(firstBalance))
        assertEquals(BalanceUpdateResult.IGNORED_OUTDATED, repository.saveIfNewer(olderBalance))

        assertEquals(firstBalance, repository.findByAccountId(accountId))
    }

    private fun balance(
        updatedAtMicros: Long,
        transactionId: UUID,
        amount: String,
    ): AccountBalance =
        AccountBalance(
            id = accountId,
            owner = ownerId,
            balance = Money(amount = BigDecimal(amount), currency = "BRL"),
            updatedAtMicros = updatedAtMicros,
            lastTransactionId = transactionId,
        )
}
