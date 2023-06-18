package todo.app.testing.api.rest.integration.regress.positive

import io.qameta.allure.Feature
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.junit.jupiter.Testcontainers
import todo.app.testing.api.rest.config.TodoAppInDockerConfiguration
import todo.app.testing.api.rest.config.TodoRestClientConfiguration
import todo.app.testing.api.rest.dto.TodoEntity
import todo.app.testing.api.rest.impl.TodoRestApi

@Tag("regress")
@Tag("post-todo")
@Testcontainers
@DirtiesContext
@Feature("POST todos just works")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class PostTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    @BeforeEach
    fun healthCheck() {
        todoRestApi.checkHealth()
    }

    @Test
    fun `single POST todo works by default`() {
        assertDoesNotThrow("POST with entity should work") {
            todoRestApi.post(TodoEntity(0, "Проверить POST новых сущностей", true))
        }
    }

    @Test
    fun `multiple POST todo works by default`() {
        assertDoesNotThrow("POST with entity should work") {
            for (i in 1..5) todoRestApi.post(
                TodoEntity(i.toLong(), "Проверить множественный POST новых сущностей", true)
            )
        }
    }
}
