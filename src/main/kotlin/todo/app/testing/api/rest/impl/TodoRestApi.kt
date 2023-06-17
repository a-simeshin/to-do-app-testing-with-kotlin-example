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

    fun get(
        conditionFactory: ConditionFactory,
        responseMatcher: Matcher<Collection<TodoEntity>>
    ): List<TodoEntity> {
        // Since this method is used in the health-check method, in order to avoid a cyclic call,
        // health-check is not called in this case
        // todo add health-check call function after implementing /health endpoint, see issues.md

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

    fun get(
        offset: Long,
        limit: Long,
        conditionFactory: ConditionFactory,
        responseMatcher: Matcher<Collection<TodoEntity>>
    ): List<TodoEntity> {
        checkHealth()

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

    fun getWithOffset(
        offset: Long,
        conditionFactory: ConditionFactory,
        responseMatcher: Matcher<Collection<TodoEntity>>
    ): List<TodoEntity> {
        checkHealth()

        val result = AtomicReference<List<TodoEntity>>()
        val stepName =
            "Waiting for get todos with offset for condition: " + responseMatcher.toDescription()
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

    @Step("POST todoEntity")
    fun post(todoEntity: TodoEntity): Any {
        checkHealth()
        return todoRestClient.post(todoEntity)
    }

    @Step("PUT todoEntity")
    fun put(todoEntity: TodoEntity): Any {
        checkHealth()
        return todoRestClient.put(todoEntity)
    }

    @Step("DELETE todoEntity by id")
    fun delete(todoEntityId: Any): Any {
        checkHealth()
        return todoRestClient.delete(todoEntityId)
    }

    @Step("Health check")
    fun checkHealth() {
        // In healthcheck purposes call get method wo params
        // todo: switch to endpoint /healthcheck when its released see issues.md
        this.get(Awaitility.await(), IsCollectionWithSize.hasSize(greaterThan(-1)))
    }
}

private fun <V> AtomicReference<V>.saveToAtomicThenReturn(anything: V): V {
    this.set(anything)
    return anything
}

private fun <T> Matcher<T>.toDescription(): String {
    val stringDescription = StringDescription()
    this.describeTo(stringDescription)
    return stringDescription.toString()
}
