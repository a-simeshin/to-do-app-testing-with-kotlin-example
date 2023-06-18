package todo.app.testing.api.rest.impl

import io.qameta.allure.Allure
import io.qameta.allure.Step
import java.util.concurrent.atomic.AtomicReference
import org.awaitility.Awaitility
import org.awaitility.core.ConditionEvaluationLogger
import org.awaitility.core.ConditionFactory
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.StringDescription
import org.hamcrest.collection.IsCollectionWithSize
import org.slf4j.LoggerFactory
import todo.app.testing.api.rest.dto.TodoEntity

class TodoRestApi(private val todoRestClient: TodoRestClient) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Retrieves a list of TodoEntity objects that match the specified responseMatcher condition.
     *
     * This method performs a wait operation using the conditionFactory and responseMatcher
     * parameters. It waits until the condition specified by responseMatcher is met, and then
     * returns the result. The wait operation is wrapped within an Allure.step for reporting
     * purposes.
     *
     * @param conditionFactory The ConditionFactory instance used for defining the wait condition.
     * @param responseMatcher The Matcher used to match the desired response condition.
     * @return A List of TodoEntity objects that match the specified responseMatcher condition.
     */
    fun get(
        conditionFactory: ConditionFactory,
        responseMatcher: Matcher<Collection<TodoEntity>>
    ): List<TodoEntity> {
        val result = AtomicReference<List<TodoEntity>>()
        val stepName = "Waiting for get todos for condition: ${responseMatcher.toDescription()}"
        Allure.step(
            stepName,
            Allure.ThrowableRunnable {
                conditionFactory
                    .await(stepName)
                    .conditionEvaluationListener(ConditionEvaluationLogger(log::info))
                    .failFast(FailFastThrowable(todoRestClient))
                    .until(
                        {
                            return@until result.saveToAtomicThenReturn(todoRestClient.get())
                        },
                        `is`(responseMatcher)
                    )
            }
        )
        return result.get()
    }

    /**
     * Retrieves a list of TodoEntity objects with the specified offset and limit, that match the
     * specified responseMatcher condition.
     *
     * This method performs a wait operation using the conditionFactory and responseMatcher
     * parameters. It waits until the condition specified by responseMatcher is met, and then
     * returns the result. The wait operation is wrapped within an Allure.step for reporting
     * purposes.
     *
     * @param offset The offset value for the get operation.
     * @param limit The limit value for the get operation.
     * @param conditionFactory The ConditionFactory instance used for defining the wait condition.
     * @param responseMatcher The Matcher used to match the desired response condition.
     * @return A List of TodoEntity objects that match the specified responseMatcher condition.
     */
    fun get(
        offset: Long,
        limit: Long,
        conditionFactory: ConditionFactory,
        responseMatcher: Matcher<Collection<TodoEntity>>
    ): List<TodoEntity> {
        val result = AtomicReference<List<TodoEntity>>()
        val stepName =
            "Waiting for get todos with offset and limit for condition: ${responseMatcher.toDescription()}"
        Allure.step(
            stepName,
            Allure.ThrowableRunnable {
                conditionFactory
                    .await(stepName)
                    .conditionEvaluationListener(ConditionEvaluationLogger(log::info))
                    .until(
                        {
                            return@until result.saveToAtomicThenReturn(
                                todoRestClient.get(offset, limit)
                            )
                        },
                        `is`(responseMatcher)
                    )
            }
        )
        return result.get()
    }

    /**
     * Retrieves a list of `TodoEntity` objects with the specified offset, based on the provided
     * condition factory and response matcher.
     *
     * @param offset The offset value for retrieving the list of `TodoEntity` objects.
     * @param conditionFactory The `ConditionFactory` used for awaiting the condition.
     * @param responseMatcher The `Matcher` used for matching the response collection of
     * `TodoEntity` objects.
     * @return The list of `TodoEntity` objects matching the specified conditions.
     */
    fun getWithOffset(
        offset: Long,
        conditionFactory: ConditionFactory,
        responseMatcher: Matcher<Collection<TodoEntity>>
    ): List<TodoEntity> {
        val result = AtomicReference<List<TodoEntity>>()
        val stepName =
            "Waiting for get todos with offset for condition: ${responseMatcher.toDescription()}"
        Allure.step(
            stepName,
            Allure.ThrowableRunnable {
                conditionFactory
                    .await(stepName)
                    .conditionEvaluationListener(ConditionEvaluationLogger(log::info))
                    .until(
                        {
                            return@until result.saveToAtomicThenReturn(
                                todoRestClient.getWithOffset(offset)
                            )
                        },
                        `is`(responseMatcher)
                    )
            }
        )
        return result.get()
    }

    /**
     * Retrieves a list of `TodoEntity` objects with the specified limit, based on the provided
     * condition factory and response matcher.
     *
     * @param limit The limit value for retrieving the list of `TodoEntity` objects.
     * @param conditionFactory The `ConditionFactory` used for awaiting the condition.
     * @param responseMatcher The `Matcher` used for matching the response collection of
     * `TodoEntity` objects.
     * @return The list of `TodoEntity` objects matching the specified conditions.
     */
    fun getWithLimit(
        limit: Long,
        conditionFactory: ConditionFactory,
        responseMatcher: Matcher<Collection<TodoEntity>>
    ): List<TodoEntity> {
        val result = AtomicReference<List<TodoEntity>>()
        val stepName =
            "Waiting for GET todos with limit for condition: " + responseMatcher.toDescription()
        Allure.step(
            stepName,
            Allure.ThrowableRunnable {
                conditionFactory
                    .await(stepName)
                    .conditionEvaluationListener(ConditionEvaluationLogger(log::info))
                    .until(
                        {
                            return@until result.saveToAtomicThenReturn(
                                todoRestClient.getWithLimit(limit)
                            )
                        },
                        `is`(responseMatcher)
                    )
            }
        )
        return result.get()
    }

    /**
     * Posts a `TodoEntity` object to the server.
     *
     * @param todoEntity The `TodoEntity` object to be posted.
     * @return The response from the server.
     */
    @Step("POST todoEntity")
    fun post(todoEntity: TodoEntity): Any {
        return todoRestClient.post(todoEntity)
    }

    /**
     * Updates a `TodoEntity` object on the server using the PUT method.
     *
     * @param todoEntity The `TodoEntity` object to be updated.
     * @return The response from the server.
     */
    @Step("PUT todoEntity")
    fun put(todoEntity: TodoEntity): Any {
        return todoRestClient.put(todoEntity)
    }

    /**
     * Deletes a `TodoEntity` object from the server by its ID.
     *
     * @param todoEntityId The ID of the `TodoEntity` object to be deleted.
     * @return The response from the server.
     */
    @Step("DELETE todoEntity by id")
    fun delete(todoEntityId: Any): Any {
        return todoRestClient.delete(todoEntityId)
    }

    /**
     * Performs a health check by calling the GET method without any parameters.
     *
     * Note: This method is currently using a workaround for health check purposes. It calls the
     * `get` method with default parameters. In the future, it should be updated to use the
     * `/health` endpoint once it is released. Refer to the `issues.md` file for more details.
     */
    @Step("Health check")
    fun checkHealth() {
        // In healthcheck purposes call get method wo params
        // todo: switch to endpoint /health when its released see issues.md
        this.get(Awaitility.await(), IsCollectionWithSize.hasSize(greaterThan(-1)))
    }
}

/**
 * Sets the value of the `AtomicReference` to the specified `anything` and returns the same value.
 *
 * @param anything The value to be set in the `AtomicReference`.
 * @return The same value that was set in the `AtomicReference`.
 */
private fun <V> AtomicReference<V>.saveToAtomicThenReturn(anything: V): V {
    this.set(anything)
    return anything
}

/**
 * Converts the `Matcher` object to its description string representation.
 *
 * @return The description string of the `Matcher` object.
 */
private fun <T> Matcher<T>.toDescription(): String {
    val stringDescription = StringDescription()
    this.describeTo(stringDescription)
    return stringDescription.toString()
}
