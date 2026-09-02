package br.com.itau.challenge.balance.adapter.input.web

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import software.amazon.awssdk.core.exception.SdkClientException
import kotlin.test.assertEquals

class RestExceptionHandlerTest {
    @Test
    fun `maps DynamoDB SDK failures to service unavailable`() {
        val response = RestExceptionHandler().handleSdkException(SdkClientException.builder().message("offline").build())

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.statusCode)
    }
}
