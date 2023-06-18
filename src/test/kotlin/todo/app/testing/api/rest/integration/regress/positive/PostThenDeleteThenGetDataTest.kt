package todo.app.testing.api.rest.integration.regress.positive

import io.qameta.allure.Feature
import org.awaitility.Awaitility
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.junit.jupiter.Testcontainers
import todo.app.testing.api.rest.config.TodoAppInDockerConfiguration
import todo.app.testing.api.rest.config.TodoRestClientConfiguration
import todo.app.testing.api.rest.dto.TodoEntity
import todo.app.testing.api.rest.impl.TodoRestApi
import todo.app.testing.api.rest.matcher.TodoEntityResponseMatcher.*

@Tag("regress")
@Tag("post-todo")
@Tag("get-todo")
@Tag("delete-todo")
@Testcontainers
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Feature("POST then DELETE then GET todos works with single data")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class PostThenDeleteThenGetDataTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    var todoEntity = TodoEntity(1, "Первое дело", false)

    @BeforeEach
    fun healthCheck() {
        todoRestApi.checkHealth()
        todoEntity = TodoEntity(1, "Первый кейс на обновлени POST then DELETE then GET", false)
        todoRestApi.post(todoEntity)
    }

    @Test
    @Order(1)
    fun `POST TODO then DELETE then GET without query parameters - Expecting 0 TODO`() {
        assertDoesNotThrow("POST then DELETE then GET should provide 0 data") {
            todoRestApi.delete(todoEntity.id)
            todoRestApi.get(conditionFactory = Awaitility.await(), responseMatcher = hasSize(0))
        }
    }
}
