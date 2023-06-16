package todo.app.testing.api.rest.integration.smoke

import io.qameta.allure.Description
import io.qameta.allure.Feature
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers
import todo.app.testing.api.rest.config.TodoAppInDockerConfiguration
import todo.app.testing.api.rest.config.TodoRestClientConfiguration
import todo.app.testing.api.rest.impl.TodoRestApi

@Tag("smoke")
@Testcontainers
@Feature("application is observable and ready for integration tests")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class SmokeObservabilityTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    @Test
    @Description("The test waits for the application to be observable via REST")
    fun `todo application is observable`() {
        assertDoesNotThrow { todoRestApi.checkHealth() }
    }
}
