package todo.app.testing.api.rest.integration.regress.positive

import io.qameta.allure.Feature
import org.awaitility.Awaitility
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.junit.jupiter.Testcontainers
import todo.app.testing.api.rest.config.TodoAppInDockerConfiguration
import todo.app.testing.api.rest.config.TodoRestClientConfiguration
import todo.app.testing.api.rest.dto.TodoEntity
import todo.app.testing.api.rest.impl.TodoRestApi
import todo.app.testing.api.rest.matcher.TodoEntityResponseMatcher
import todo.app.testing.api.rest.matcher.TodoEntityResponseMatcher.*

@Tag("regress")
@Tag("put-todo")
@Tag("get-todo")
@Tag("post-todo")
@Testcontainers
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Feature("POST then PUT then GET todos works with single data")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class PostThenPutThenGetSingleDataTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    var todoEntity = TodoEntity(1, "Первое дело", false)

    @BeforeEach
    fun healthCheck() {
        todoRestApi.checkHealth()
        todoEntity = TodoEntity(1, "Первый кейс на обновлени POST then PUT then GET", false)
        todoRestApi.post(todoEntity)
    }

    @AfterEach
    fun cleanup() {
        todoRestApi.delete(todoEntity.id)
    }

    @Test
    @Order(1)
    fun `POST TODO then PUT then GET without query parameters - Expecting TODO with new TEXT field`() {
        todoEntity = TodoEntity(1, "Обновленный кейс на обновлени POST then PUT then GET", false)
        todoRestApi.put(todoEntity)
        assertDoesNotThrow("POST then PUT then GET should provide updated version") {
            todoRestApi.get(
                conditionFactory = Awaitility.await(),
                responseMatcher = TodoEntityResponseMatcher(expecting = todoEntity)
            )
        }
    }

    @Test
    @Order(2)
    fun `POST TODO then PUT then GET without query parameters - Expecting TODO with new COMPLETED field`() {
        todoEntity = TodoEntity(1, "Первый кейс на обновлени POST then PUT then GET", true)
        todoRestApi.put(todoEntity)
        assertDoesNotThrow("POST then PUT then GET should provide updated version") {
            todoRestApi.get(
                conditionFactory = Awaitility.await(),
                responseMatcher = TodoEntityResponseMatcher(expecting = todoEntity)
            )
        }
    }

    @Test
    @Order(3)
    fun `POST TODO then PUT then GET without query parameters - Expecting TODO with new TEXT and COMPLETED field`() {
        todoEntity = TodoEntity(1, "Обновленный кейс на обновлени POST then PUT then GET", true)
        todoRestApi.put(todoEntity)
        assertDoesNotThrow("POST then PUT then GET should provide updated version") {
            todoRestApi.get(
                conditionFactory = Awaitility.await(),
                responseMatcher = TodoEntityResponseMatcher(expecting = todoEntity)
            )
        }
    }

    @Test
    @Order(4)
    fun `POST the same TODO then PUT then GET without query parameters - Expecting the same TODO - No changes `() {
        todoRestApi.put(todoEntity)
        assertDoesNotThrow("POST the same then PUT then GET should version without changes") {
            todoRestApi.get(
                conditionFactory = Awaitility.await(),
                responseMatcher = TodoEntityResponseMatcher(expecting = todoEntity)
            )
        }
    }
}
