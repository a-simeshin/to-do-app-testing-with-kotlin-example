package todo.app.testing.api.rest.integration.regress.negative

import io.qameta.allure.Feature
import java.math.BigInteger
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.HttpClientErrorException
import org.testcontainers.junit.jupiter.Testcontainers
import todo.app.testing.api.rest.config.TodoAppInDockerConfiguration
import todo.app.testing.api.rest.config.TodoRestClientConfiguration
import todo.app.testing.api.rest.impl.TodoRestApi
import todo.app.testing.api.rest.impl.TodoRestClient

@Tag("regress")
@Tag("negative")
@Tag("get-todo")
@Testcontainers
@DirtiesContext
@Feature("GET todos works wo data")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class NegativeGetTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    @Autowired lateinit var todoRestClient: TodoRestClient

    val moreThanLong = BigInteger.valueOf(Long.MAX_VALUE).plus(BigInteger.TEN).toString()
    val nan = "Not an unsigned 64-bit identifier"

    @BeforeEach
    fun healthCheck() {
        todoRestApi.checkHealth()
    }

    @Test
    fun `GET correct response for offset = -1`() {
        assertThat(
            "GET return correct information about exceptional situation when offset = -1",
            assertThrows<HttpClientErrorException> { todoRestClient.getWithOffset(-1) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    fun `GET correct response for limit = -1`() {
        assertThat(
            "GET return correct information about exceptional situation when limit = -1",
            assertThrows<HttpClientErrorException> { todoRestClient.getWithLimit(-1) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    fun `GET correct response for offset = -1 and limit = -1`() {
        assertThat(
            "GET return correct information about exceptional situation when offset = -1 and limit = -1",
            assertThrows<HttpClientErrorException> { todoRestClient.get(-1, -1) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    @Disabled("Missing restriction, see issues.md for details")
    fun `GET correct response for offset value more than Long MAX value`() {
        assertThat(
            "GET return correct information about exceptional situation when offset value more than Long MAX value",
            assertThrows<HttpClientErrorException> { todoRestClient.getWithOffset(moreThanLong) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    @Disabled("Missing restriction, see issues.md for details")
    fun `GET correct response for limit value more than Long MAX value`() {
        assertThat(
            "GET return correct information about exceptional situation when limit value more than Long MAX value",
            assertThrows<HttpClientErrorException> { todoRestClient.getWithLimit(moreThanLong) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    @Disabled("Missing restriction, see issues.md for details")
    fun `GET correct response for offset and limit value more than Long MAX value`() {
        assertThat(
            "GET return correct information about exceptional situation when offset and limit value more than Long MAX value",
            assertThrows<HttpClientErrorException> {
                todoRestClient.get(moreThanLong, moreThanLong)
            },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    fun `GET correct response for offset = null`() {
        assertThat(
            "GET return correct information about exceptional situation when offset = null",
            assertThrows<HttpClientErrorException> { todoRestClient.getWithOffset(null) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    fun `GET correct response for limit = null`() {
        assertThat(
            "GET return correct information about exceptional situation when limit = null",
            assertThrows<HttpClientErrorException> { todoRestClient.getWithLimit(null) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    fun `GET correct response for offset = null and limit = null`() {
        assertThat(
            "GET return correct information about exceptional situation when offset = null and limit = null",
            assertThrows<HttpClientErrorException> { todoRestClient.get(null, null) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    fun `GET correct response for offset = NaN`() {
        assertThat(
            "GET return correct information about exceptional situation when offset = NaN",
            assertThrows<HttpClientErrorException> { todoRestClient.getWithOffset(nan) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    fun `GET correct response for limit = NaN`() {
        assertThat(
            "GET return correct information about exceptional situation when limit = NaN",
            assertThrows<HttpClientErrorException> { todoRestClient.getWithLimit(nan) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }

    @Test
    fun `GET correct response for offset = NaN and limit = NaN`() {
        assertThat(
            "GET return correct information about exceptional situation when offset = NaN and limit = NaN",
            assertThrows<HttpClientErrorException> { todoRestClient.get(nan, nan) },
            hasProperty("message", equalTo("400 Bad Request: \"Invalid query string\""))
        )
    }
}
