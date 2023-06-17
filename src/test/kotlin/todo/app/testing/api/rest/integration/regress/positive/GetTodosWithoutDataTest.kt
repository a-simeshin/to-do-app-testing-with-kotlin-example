package todo.app.testing.api.rest.integration.regress.positive

import io.qameta.allure.Feature
import org.awaitility.Awaitility
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.junit.jupiter.Testcontainers
import todo.app.testing.api.rest.config.TodoAppInDockerConfiguration
import todo.app.testing.api.rest.config.TodoRestClientConfiguration
import todo.app.testing.api.rest.impl.TodoRestApi

@Tag("regress")
@Tag("get-todo")
@Testcontainers
@DirtiesContext
@Feature("GET todos works wo data")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class GetTodosWithoutDataTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    @Test
    fun `todo application GET works by default without query parameters`() {
        assertDoesNotThrow {
            todoRestApi.get(conditionFactory = Awaitility.await(), responseMatcher = hasSize(0))
        }
    }

    @Test
    fun `todo application GET works by default with offset = 0 and limit = 0 wo data`() {
        assertDoesNotThrow {
            todoRestApi.get(
                offset = 0,
                limit = 0,
                conditionFactory = Awaitility.await(),
                responseMatcher = hasSize(0)
            )
        }
    }

    @Test
    fun `todo application GET works by default with offset = 0 wo data`() {
        assertDoesNotThrow {
            todoRestApi.getWithOffset(
                offset = 0,
                conditionFactory = Awaitility.await(),
                responseMatcher = hasSize(0)
            )
        }
    }

    @Test
    fun `todo application GET works by default with limit = 0 wo data`() {
        assertDoesNotThrow {
            todoRestApi.getWithLimit(
                limit = 0,
                conditionFactory = Awaitility.await(),
                responseMatcher = hasSize(0)
            )
        }
    }
}
