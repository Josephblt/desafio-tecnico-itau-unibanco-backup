package br.com.itau.challenge.balance.adapter.input.web

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import software.amazon.awssdk.core.exception.SdkException

@RestControllerAdvice
class RestExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(SdkException::class)
    fun handleSdkException(exception: SdkException): ResponseEntity<Unit> {
        logger.error("DynamoDB dependency failure", exception)
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()
    }
}
