package br.com.itau.challenge.balance.adapter.input.web

import br.com.itau.challenge.balance.BalanceFixtures
import br.com.itau.challenge.balance.port.input.GetBalanceUseCase
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class BalanceControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @MockitoBean
    private lateinit var getBalanceUseCase: GetBalanceUseCase

    @Test
    fun `returns latest balance for account`() {
        given(getBalanceUseCase.getBalance(BalanceFixtures.accountId)).willReturn(BalanceFixtures.balance())

        mockMvc.get("/balances/${BalanceFixtures.accountId}").andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(BalanceFixtures.accountId.toString()) }
            jsonPath("$.owner") { value(BalanceFixtures.ownerId.toString()) }
            jsonPath("$.balance.amount") { value(183.12) }
            jsonPath("$.balance.currency") { value("BRL") }
            jsonPath("$.updated_at") { value("2025-07-05T18:04:13.433-03:00") }
        }
    }

    @Test
    fun `returns bad request for invalid account id`() {
        mockMvc.get("/balances/not-a-uuid").andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `returns not found for unknown account`() {
        given(getBalanceUseCase.getBalance(BalanceFixtures.accountId)).willReturn(null)

        mockMvc.get("/balances/${BalanceFixtures.accountId}").andExpect {
            status { isNotFound() }
        }
    }
}
