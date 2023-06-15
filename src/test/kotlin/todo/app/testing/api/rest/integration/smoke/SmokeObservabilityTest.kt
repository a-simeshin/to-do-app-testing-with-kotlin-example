package todo.app.testing.api.rest.integration.smoke

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import todo.app.testing.api.rest.config.TodoRestClientConfiguration
import todo.app.testing.api.rest.impl.TodoRestApi
import todo.app.testing.api.rest.impl.TodoRestClient

@SpringBootTest(classes = [TodoRestClientConfiguration::class])
class SmokeObservabilityTest {

    @Autowired lateinit var todoRestApi: TodoRestApi
    @Autowired lateinit var todoRestClient: TodoRestClient

    @Test fun `todo application is observable`() {
        assertDoesNotThrow { todoRestClient.get() }
    }
}