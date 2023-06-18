package todo.app.testing.api.rest.integration.regress.negative

import io.qameta.allure.Feature
import io.qameta.allure.Issue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.HttpClientErrorException
import org.testcontainers.junit.jupiter.Testcontainers
import todo.app.testing.api.rest.config.TodoAppInDockerConfiguration
import todo.app.testing.api.rest.config.TodoRestClientConfiguration
import todo.app.testing.api.rest.dto.TodoEntity
import todo.app.testing.api.rest.impl.TodoRestApi
import todo.app.testing.api.rest.impl.TodoRestClient

@Tag("regress")
@Tag("negative")
@Tag("put-todo")
@Testcontainers
@DirtiesContext
@Feature("Exception handling PUT todos")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class NegativePutTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    @Autowired lateinit var todoRestClient: TodoRestClient

    @BeforeEach
    fun healthCheck() {
        todoRestApi.checkHealth()
    }

    @Test
    @Issue("Missing detailed description, see issues.md for details")
    fun `PUT correct response for non-existing entity`() {
        assertThat(
            "PUT return correct information about exceptional situation for non-existing entity",
            assertThrows<HttpClientErrorException> {
                todoRestClient.put(TodoEntity(999, "text", false))
            },
            hasProperty("message", containsString("404 Not Found"))
        )
    }

    @Test
    @Disabled("Missing restriction, see issues.md for details")
    fun `PUT correct response for entity with id = -1`() {
        assertThat(
            "PUT return correct information about exceptional situation for entity with id = -",
            assertThrows<HttpClientErrorException> {
                todoRestClient.put(TodoEntity(-1, "text", false))
            },
            hasProperty("message", containsString("404 Not Found"))
        )
    }

    // TODO add test for null or empty String update after adding restriction for field TEXT

}
