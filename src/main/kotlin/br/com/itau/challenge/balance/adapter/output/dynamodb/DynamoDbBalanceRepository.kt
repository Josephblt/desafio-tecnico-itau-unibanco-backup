package br.com.itau.challenge.balance.adapter.output.dynamodb

import br.com.itau.challenge.balance.domain.model.AccountBalance
import br.com.itau.challenge.balance.domain.model.BalanceUpdateResult
import br.com.itau.challenge.balance.domain.model.Money
import br.com.itau.challenge.balance.port.output.BalanceProvider
import br.com.itau.challenge.balance.port.output.BalanceRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import java.math.BigDecimal
import java.util.UUID

private const val ID = "id"
private const val OWNER = "owner"
private const val BALANCE_AMOUNT = "balanceAmount"
private const val BALANCE_CURRENCY = "balanceCurrency"
private const val UPDATED_AT_MICROS = "updatedAtMicros"
private const val LAST_TRANSACTION_ID = "lastTransactionId"

@Component
class DynamoDbBalanceRepository(
    private val dynamoDbClient: DynamoDbClient,
    @Value("\${dynamodb.table-name}") private val tableName: String,
) : BalanceRepository,
    BalanceProvider {
    override fun saveIfNewer(balance: AccountBalance): BalanceUpdateResult {
        val request =
            UpdateItemRequest
                .builder()
                .tableName(tableName)
                .key(mapOf(ID to AttributeValue.builder().s(balance.id.toString()).build()))
                .updateExpression(
                    "SET #owner = :owner, #amount = :amount, #currency = :currency, " +
                        "#updatedAtMicros = :updatedAtMicros, #lastTransactionId = :lastTransactionId",
                ).conditionExpression(
                    "attribute_not_exists(#id) OR #updatedAtMicros < :updatedAtMicros OR #lastTransactionId = :lastTransactionId",
                ).expressionAttributeNames(
                    mapOf(
                        "#id" to ID,
                        "#owner" to OWNER,
                        "#amount" to BALANCE_AMOUNT,
                        "#currency" to BALANCE_CURRENCY,
                        "#updatedAtMicros" to UPDATED_AT_MICROS,
                        "#lastTransactionId" to LAST_TRANSACTION_ID,
                    ),
                ).expressionAttributeValues(
                    mapOf(
                        ":owner" to AttributeValue.builder().s(balance.owner.toString()).build(),
                        ":amount" to AttributeValue.builder().n(balance.balance.amount.toPlainString()).build(),
                        ":currency" to AttributeValue.builder().s(balance.balance.currency).build(),
                        ":updatedAtMicros" to AttributeValue.builder().n(balance.updatedAtMicros.toString()).build(),
                        ":lastTransactionId" to AttributeValue.builder().s(balance.lastTransactionId.toString()).build(),
                    ),
                ).build()

        return try {
            dynamoDbClient.updateItem(request)
            BalanceUpdateResult.UPDATED
        } catch (_: ConditionalCheckFailedException) {
            BalanceUpdateResult.IGNORED_OUTDATED
        }
    }

    override fun findByAccountId(accountId: UUID): AccountBalance? {
        val request =
            GetItemRequest
                .builder()
                .tableName(tableName)
                .key(mapOf(ID to AttributeValue.builder().s(accountId.toString()).build()))
                .build()

        val response = dynamoDbClient.getItem(request)
        if (!response.hasItem()) return null

        val item = response.item()
        return AccountBalance(
            id = UUID.fromString(item.getValue(ID).s()),
            owner = UUID.fromString(item.getValue(OWNER).s()),
            balance =
                Money(
                    amount = BigDecimal(item.getValue(BALANCE_AMOUNT).n()),
                    currency = item.getValue(BALANCE_CURRENCY).s(),
                ),
            updatedAtMicros = item.getValue(UPDATED_AT_MICROS).n().toLong(),
            lastTransactionId = UUID.fromString(item.getValue(LAST_TRANSACTION_ID).s()),
        )
    }
}
