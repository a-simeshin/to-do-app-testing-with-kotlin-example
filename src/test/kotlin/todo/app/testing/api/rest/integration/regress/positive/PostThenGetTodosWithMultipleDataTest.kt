package todo.app.testing.api.rest.integration.regress.positive

import io.qameta.allure.Feature
import java.util.concurrent.TimeUnit
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

@Suppress("SameParameterValue")
@Tag("regress")
@Tag("get-todo")
@Tag("post-todo")
@Testcontainers
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Feature("POST data then GET todos works with multiple data")
@SpringBootTest(classes = [TodoRestClientConfiguration::class, TodoAppInDockerConfiguration::class])
class PostThenGetTodosWithMultipleDataTest {

    @Autowired lateinit var todoRestApi: TodoRestApi

    @BeforeEach
    fun healthCheck() {
        todoRestApi.checkHealth()
        postTodo(5)
    }

    @AfterEach
    fun cleanup() {
        getIdToDelete(5).forEach { todoRestApi.delete(it) }
    }

    @Test
    @Order(1)
    fun `POST 5 TODO then GET without query parameters - Expecting 5 TODO`() {
        assertDoesNotThrow("GET without query parameters should not hide data") {
            todoRestApi.get(conditionFactory = Awaitility.await(), responseMatcher = hasSize(5))
        }
    }

    @Test
    @Order(2)
    fun `POST 5 TODO then GET with offset = 0 limit = 5 - Then wait 1 SEC - Expecting 5 TODO`() {
        assertDoesNotThrow(
            "limit = 5 and offset = 0 should provide saved data with 5 length if its saved"
        ) {
            todoRestApi.get(
                offset = 0,
                limit = 5,
                conditionFactory = Awaitility.await().pollDelay(1, TimeUnit.SECONDS),
                responseMatcher = hasSize(5)
            )
        }
    }

    @Test
    @Order(3)
    fun `POST 5 TODO then GET with offset = 0 limit = 3 - Expecting 3 TODO`() {
        assertDoesNotThrow("Limit = 3 and offset = 0 should provide at least 3 TODO if its saved") {
            todoRestApi.get(
                offset = 0,
                limit = 3,
                conditionFactory = Awaitility.await(),
                responseMatcher = hasSize(3)
            )
        }
    }

    @Test
    @Order(4)
    fun `POST 5 TODO then GET with offset = 2 limit = 5 - Expecting 3 TODO`() {
        assertDoesNotThrow("Offset = 2 and limit = 0 should provide data size - offset value") {
            todoRestApi.get(
                offset = 2,
                limit = 5,
                conditionFactory = Awaitility.await().pollDelay(1, TimeUnit.SECONDS),
                responseMatcher = hasSize(5 - 2)
            )
        }
    }

    @Test
    @Order(5)
    fun `POST 5 TODO then GET with offset = 5 limit = 5 - Then wait 1 SEC - Expecting 0 TODO cause offset = 1`() {
        assertDoesNotThrow("Offset = 5 and limit = 5 should hide all 5 saved data in list") {
            todoRestApi.get(
                offset = 5,
                limit = 5,
                conditionFactory = Awaitility.await().pollDelay(1, TimeUnit.SECONDS),
                responseMatcher = hasSize(0)
            )
        }
    }

    @Test
    @Order(6)
    fun `POST 5 TODO then GET with offset = 0 - Expecting 5 TODO with correct fields`() {
        assertDoesNotThrow("Offset = 0 should provide all saved data") {
            todoRestApi.getWithOffset(
                offset = 0,
                conditionFactory = Awaitility.await(),
                responseMatcher = hasSize(5)
            )
        }
    }

    @Test
    @Order(7)
    fun `POST 5 TODO then GET with offset = 6 - Then wait 1 SEC - Expecting 0 TODO cause offset = 6`() {
        assertDoesNotThrow("Offset size more or equal to saved data size should hide all data") {
            todoRestApi.getWithOffset(
                offset = 6,
                conditionFactory = Awaitility.await().pollDelay(1, TimeUnit.SECONDS),
                responseMatcher = hasSize(0)
            )
        }
    }

    @Test
    @Order(8)
    fun `POST 5 TODO then GET with limit = 0 - Then wait 1 SEC - Expecting 0 TODO cause limit = 0`() {
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
    fun `POST 5 TODO then GET with limit = 6 - Expecting at least 5 TODO`() {
        assertDoesNotThrow(
            "Limit equal or greater than saved data size should provide all saved data"
        ) {
            todoRestApi.getWithLimit(
                limit = 6,
                conditionFactory = Awaitility.await(),
                responseMatcher = hasSize(5)
            )
        }
    }

    @Test
    @Order(10)
    fun `POST 5 TODO then GET with limit = 2 - Expecting 2 TODO`() {
        assertDoesNotThrow("Limit more than expected data should not hide expecting data") {
            todoRestApi.getWithLimit(
                limit = 2,
                conditionFactory = Awaitility.await(),
                responseMatcher = hasSize(2)
            )
        }
    }

    private fun postTodo(counter: Int): List<TodoEntity> {
        val first = getIndexToGenerate()
        return (first until first + counter).map {
            val temp =
                TodoEntity(
                    id = it,
                    text = "Надо проверить множественное получение данных через Rest",
                    completed = false
                )
            todoRestApi.post(temp)
            listOfIds.add(it)
            temp
        }
    }

    companion object {
        @JvmStatic val listOfIds = mutableListOf<Long>()

        fun getIndexToGenerate(): Long {
            if (listOfIds.size != 0) return listOfIds.last() + 1
            return 0
        }

        fun getIdToDelete(counter: Int): List<Long> {
            if (counter >= listOfIds.size) return listOfIds
            val startIndex = listOfIds.size - counter
            return listOfIds.subList(startIndex, listOfIds.size)
        }
    }
}
