package todo.app.testing.api.rest.integration.regress.positive

import io.qameta.allure.Feature
import java.util.concurrent.TimeUnit
import org.awaitility.Awaitility
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.junit.jupiter.Testcontainers
import todo.app.testing.api.rest.config.TodoAppInDockerConfiguration
import todo.app.testing.api.rest.config.TodoRestClientConfiguration
import todo.app.testing.api.rest.dto.TodoEntity
import todo.app.testing.api.rest.impl.TodoRestApi

@Tag("regress")
@Tag("get-todo")
@Tag("post-todo")
@Testcontainers
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Feature("POST data then GET todos works with single data")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class PostThenGetTodosWithSingleDataTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    var todoEntity = TodoEntity(1, "Первое дело", false)

    @AfterEach
    fun cleanup() {
        todoRestApi.delete(todoEntity.id)
    }

    @Test
    @Order(1)
    fun `POST 1 TODO then GET without query parameters - Expecting 1 TODO with correct fields`() {
        todoEntity = TodoEntity(1, "Первый кейс на проверку POST-GET", false)
        todoRestApi.post(todoEntity)

        assertDoesNotThrow("GET without query parameters should not hide data") {
            todoRestApi.get(
                conditionFactory = Awaitility.await(),
                responseMatcher = matcherFor(entity = todoEntity)
            )
        }
    }

    @Test
    @Order(2)
    fun `POST 1 TODO then GET with offset = 0 limit = 0 - Then wait 1 SEC - Expecting 0 TODO cause limit = 0`() {
        todoEntity = TodoEntity(2, "Второй кейс на проверку POST-GET", false)
        todoRestApi.post(todoEntity)

        assertDoesNotThrow(
            "Limit size lower then expected data size should hide data even if offset = 0"
        ) {
            todoRestApi.get(
                offset = 0,
                limit = 0,
                conditionFactory = Awaitility.await().pollDelay(1, TimeUnit.SECONDS),
                responseMatcher = hasSize(0)
            )
        }
    }

    @Test
    @Order(3)
    fun `POST 1 TODO then GET with offset = 0 limit = 1 - Expecting 1 TODO with correct fields`() {
        todoEntity = TodoEntity(3, "Третий кейс на проверку POST-GET", false)
        todoRestApi.post(todoEntity)

        assertDoesNotThrow("Limit = 1 and offset = 0 should provide at least 1 TODO if its saved") {
            todoRestApi.get(
                offset = 0,
                limit = 1,
                conditionFactory = Awaitility.await(),
                responseMatcher = matcherFor(entity = todoEntity)
            )
        }
    }

    @Test
    @Order(4)
    fun `POST 1 TODO then GET with offset = 1 limit = 0 - Then wait 1 SEC - Expecting 0 TODO cause offset = 1`() {
        todoEntity = TodoEntity(4, "Четвертый кейс на проверку POST-GET", false)
        todoRestApi.post(todoEntity)

        assertDoesNotThrow("Offset = 1 and limit = 0 should hide any data") {
            todoRestApi.get(
                offset = 1,
                limit = 0,
                conditionFactory = Awaitility.await().pollDelay(1, TimeUnit.SECONDS),
                responseMatcher = hasSize(0)
            )
        }
    }

    @Test
    @Order(5)
    fun `POST 1 TODO then GET with offset = 1 limit = 1 - Then wait 1 SEC - Expecting 0 TODO cause offset = 1`() {
        todoEntity = TodoEntity(5, "Пятый кейс на проверку POST-GET", false)
        todoRestApi.post(todoEntity)

        assertDoesNotThrow("Offset = 1 and limit = 1 should hide first saved data in list") {
            todoRestApi.get(
                offset = 1,
                limit = 1,
                conditionFactory = Awaitility.await().pollDelay(1, TimeUnit.SECONDS),
                responseMatcher = hasSize(0)
            )
        }
    }

    @Test
    @Order(6)
    fun `POST 1 TODO then GET with offset = 0 - Expecting 1 TODO with correct fields`() {
        todoEntity = TodoEntity(6, "Шестой кейс на проверку POST-GET", false)
        todoRestApi.post(todoEntity)

        assertDoesNotThrow("Offset = 0 should provide all saved data") {
            todoRestApi.getWithOffset(
                offset = 0,
                conditionFactory = Awaitility.await(),
                responseMatcher = matcherFor(entity = todoEntity)
            )
        }
    }

    @Test
    @Order(7)
    fun `POST 1 TODO then GET with offset = 1 - Then wait 1 SEC - Expecting 0 TODO cause offset = 1`() {
        todoEntity = TodoEntity(7, "Седьмой кейс на проверку POST-GET", false)
        todoRestApi.post(todoEntity)

        assertDoesNotThrow("Offset size more or equal to saved data size should hide all data") {
            todoRestApi.getWithOffset(
                offset = 1,
                conditionFactory = Awaitility.await().pollDelay(1, TimeUnit.SECONDS),
                responseMatcher = hasSize(0)
            )
        }
    }

    @Test
    @Order(8)
    fun `POST 1 TODO then GET with limit = 0 - Then wait 1 SEC - Expecting 0 TODO cause limit = 0`() {
        todoEntity = TodoEntity(8, "Восьмой кейс на проверку POST-GET", false)
        todoRestApi.post(todoEntity)

        assertDoesNotThrow("Limit = 0 should hide all saved data") {
            todoRestApi.getWithLimit(
                limit = 0,
                conditionFactory = Awaitility.await().pollDelay(1, TimeUnit.SECONDS),
                responseMatcher = hasSize(0)
            )
        }
    }

    @Test
    @Order(9)
    fun `POST 1 TODO then GET with limit = 1 - Expecting 1 TODO with correct fields`() {
        todoEntity = TodoEntity(8, "Девятый кейс на проверку POST-GET", false)
        todoRestApi.post(todoEntity)

        assertDoesNotThrow("Limit = 1 should provide at least 1 TODO with correct fields") {
            todoRestApi.getWithLimit(
                limit = 1,
                conditionFactory = Awaitility.await(),
                responseMatcher = matcherFor(entity = todoEntity)
            )
        }
    }

    @Test
    @Order(10)
    fun `POST 1 TODO then GET with limit = 2 - Expecting 1 TODO with correct fields`() {
        todoEntity = TodoEntity(8, "Девятый кейс на проверку POST-GET", false)
        todoRestApi.post(todoEntity)

        assertDoesNotThrow("Limit more than expected data should not hide expecting data") {
            todoRestApi.getWithLimit(
                limit = 2,
                conditionFactory = Awaitility.await(),
                responseMatcher = matcherFor(entity = todoEntity)
            )
        }
    }
}

private fun matcherFor(entity: TodoEntity): Matcher<Collection<TodoEntity>> {
    return anyOf(
        hasItem<TodoEntity>(
            allOf(
                hasProperty("id", equalTo(entity.id)),
                hasProperty("text", equalTo(entity.text)),
                hasProperty("completed", equalTo(entity.completed))
            )
        )
    )
}
