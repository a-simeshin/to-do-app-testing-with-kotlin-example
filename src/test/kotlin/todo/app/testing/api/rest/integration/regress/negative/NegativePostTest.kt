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
@Tag("post-todo")
@Testcontainers
@DirtiesContext
@Feature("Exception handling POST todos")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class NegativePostTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    @Autowired lateinit var todoRestClient: TodoRestClient

    val moreThanLong = BigInteger.valueOf(Long.MAX_VALUE).plus(BigInteger.TEN).toString()

    @BeforeEach
    fun healthCheck() {
        todoRestApi.checkHealth()
    }

    @Test
    fun `POST correct response for null as entity`() {
        assertThat(
            "POST return correct information about exceptional situation when entity is null",
            assertThrows<HttpClientErrorException> { todoRestClient.post(null) },
            hasProperty(
                "message",
                containsString(
                    "400 Bad Request: \"Request body deserialize error: EOF while parsing a value"
                )
            )
        )
    }

    @Test
    fun `POST correct response for not a json String as entity`() {
        assertThat(
            "POST return correct information about exceptional situation when entity not a json String",
            assertThrows<HttpClientErrorException> { todoRestClient.post("Just string") },
            hasProperty(
                "message",
                containsString("400 Bad Request: \"Request body deserialize error: expected value")
            )
        )
    }

    @Test
    fun `POST correct response for entity TODO with incorrect ID more than u64`() {
        assertThat(
            "POST return correct information about exceptional situation when ID more than u64",
            assertThrows<HttpClientErrorException> {
                todoRestClient.post(FlexibleTodo(moreThanLong, "text", false))
            },
            hasProperty(
                "message",
                containsString(
                    "Request body deserialize error: invalid type: string \"9223372036854775817\", expected u64"
                )
            )
        )
    }

    @Test
    fun `POST correct response for entity TODO with incorrect ID = null`() {
        assertThat(
            "POST return correct information about exceptional situation when ID = null",
            assertThrows<HttpClientErrorException> {
                todoRestClient.post(FlexibleTodo(null, "text", false))
            },
            hasProperty(
                "message",
                containsString(
                    "400 Bad Request: \"Request body deserialize error: invalid type: null, expected u64"
                )
            )
        )
    }

    @Test
    fun `POST correct response for entity TODO with incorrect ID = -1`() {
        assertThat(
            "POST return correct information about exceptional situation when ID = -1",
            assertThrows<HttpClientErrorException> {
                todoRestClient.post(FlexibleTodo(-1, "text", false))
            },
            hasProperty(
                "message",
                containsString(
                    "400 Bad Request: \"Request body deserialize error: invalid value: integer `-1`, expected u64"
                )
            )
        )
    }

    @Test
    fun `POST correct response for entity TODO with incorrect text = null`() {
        assertThat(
            "POST return correct information about exceptional situation when text = null",
            assertThrows<HttpClientErrorException> {
                todoRestClient.post(FlexibleTodo(666, null, false))
            },
            hasProperty(
                "message",
                containsString(
                    "400 Bad Request: \"Request body deserialize error: invalid type: null, expected a string"
                )
            )
        )
    }

    @Test
    @Disabled("Missing restriction, see issues.md for details")
    fun `POST correct response for entity TODO with incorrect text = empty String`() {
        assertThat(
            "POST return correct information about exceptional situation when text = empty String",
            assertThrows<HttpClientErrorException> {
                todoRestClient.post(FlexibleTodo(666, "text", false))
            },
            hasProperty(
                "message",
                containsString(
                    "400 Bad Request: \"Request body deserialize error: invalid text content: empty String\""
                )
            )
        )
    }

    @Test
    fun `POST correct response for entity TODO with incorrect completed = null`() {
        assertThat(
            "POST return correct information about exceptional situation when completed = null",
            assertThrows<HttpClientErrorException> {
                todoRestClient.post(FlexibleTodo(666, "text", null))
            },
            hasProperty(
                "message",
                containsString(
                    "400 Bad Request: \"Request body deserialize error: invalid type: null, expected a boolean"
                )
            )
        )
    }

    @Test
    fun `POST correct response for entity TODO with incorrect completed = not a Boolean`() {
        assertThat(
            "POST return correct information about exceptional situation when completed = not a Boolean",
            assertThrows<HttpClientErrorException> {
                todoRestClient.post(FlexibleTodo(666, "text", "YEP"))
            },
            hasProperty(
                "message",
                containsString(
                    "400 Bad Request: \"Request body deserialize error: invalid type: string \"YEP\", expected a boolean"
                )
            )
        )
    }

    @Test
    fun `POST correct response for not an entity TODO`() {
        assertThat(
            "POST return correct information about exceptional situation when completed = not a Boolean",
            assertThrows<HttpClientErrorException> {
                todoRestClient.post(EvenNotTodo(666, "Cook basics for students", "Bread and beer"))
            },
            hasProperty(
                "message",
                containsString(
                    "400 Bad Request: \"Request body deserialize error: missing field `id`"
                )
            )
        )
    }

    data class FlexibleTodo(val id: Any?, val text: Any?, val completed: Any?)

    data class EvenNotTodo(val identifier: Any?, val recipe: Any?, val ingredients: Any?)
}
