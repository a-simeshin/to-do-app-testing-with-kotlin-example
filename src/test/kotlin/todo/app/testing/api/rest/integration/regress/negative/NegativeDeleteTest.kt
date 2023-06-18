package todo.app.testing.api.rest.integration.regress.negative

import io.qameta.allure.Feature
import java.math.BigInteger
import java.util.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestOperations
import org.testcontainers.junit.jupiter.Testcontainers
import todo.app.testing.api.rest.config.TodoAppInDockerConfiguration
import todo.app.testing.api.rest.config.TodoRestClientConfiguration
import todo.app.testing.api.rest.impl.TodoRestApi
import todo.app.testing.api.rest.impl.TodoRestClient

@Tag("regress")
@Tag("negative")
@Tag("delete-todo")
@Testcontainers
@DirtiesContext
@Feature("Exception handling DELETE todos")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class NegativeDeleteTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    @Autowired lateinit var todoRestClient: TodoRestClient

    @Autowired lateinit var restOperations: RestOperations

    private inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

    val moreThanLong = BigInteger.valueOf(Long.MAX_VALUE).plus(BigInteger.TEN).toString()

    @BeforeEach
    fun healthCheck() {
        todoRestApi.checkHealth()
    }

    @Test
    @Disabled("No restriction, see issues.md")
    fun `DELETE correct response for id = -1`() {
        assertThat(
            "DELETE return correct information about exceptional situation when id = -1",
            assertThrows<HttpClientErrorException> { todoRestClient.delete(-1) },
            hasProperty(
                "message",
                containsString("400 Bad Request: \"Invalid query string: negative id\"")
            )
        )
    }

    @Test
    @Disabled("No restriction, see issues.md")
    fun `DELETE correct response for id = more than u64`() {
        assertThat(
            "DELETE return correct information about exceptional situation when id = more than u64",
            assertThrows<HttpClientErrorException> { todoRestClient.delete(moreThanLong) },
            hasProperty(
                "message",
                containsString("400 Bad Request: \"Invalid query string: id more than 64u\"")
            )
        )
    }

    @Test
    @Disabled("No restriction, see issues.md")
    fun `DELETE correct response for id = null`() {
        assertThat(
            "DELETE return correct information about exceptional situation when offset = null",
            assertThrows<HttpClientErrorException> { todoRestClient.delete(null) },
            hasProperty(
                "message",
                containsString("400 Bad Request: \"Invalid query string: id is null\"")
            )
        )
    }

    @Test
    @Disabled("No restriction, see issues.md")
    fun `DELETE correct response for id = just a String`() {
        assertThat(
            "DELETE return correct information about exceptional situation when offset = null",
            assertThrows<HttpClientErrorException> { todoRestClient.delete("just a String") },
            hasProperty(
                "message",
                containsString(
                    "400 Bad Request: \"Invalid query string: id should be a 64u formatted String\""
                )
            )
        )
    }

    @Test
    fun `DELETE correct response for unknown user credentials via Basic Auth`() {
        val httpHeaders = HttpHeaders()
        httpHeaders.accept = Collections.singletonList(MediaType.APPLICATION_JSON)
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.setBasicAuth("unknown", "unknown")

        assertThat(
            "DELETE return correct information about exceptional situation when unknown user credentials used via Basic Auth",
            assertThrows<HttpClientErrorException> {
                restOperations.exchange(
                    "/todos/0",
                    HttpMethod.DELETE,
                    HttpEntity<Any>(httpHeaders),
                    typeReference<String>(),
                )
            },
            hasProperty("message", containsString("401 Unauthorized"))
        )
    }
}
