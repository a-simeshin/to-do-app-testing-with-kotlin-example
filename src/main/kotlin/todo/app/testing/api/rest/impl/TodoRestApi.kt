package todo.app.testing.api.rest.impl

import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.http.*
import org.awaitility.Awaitility
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import todo.app.testing.api.rest.dto.TodoEntity
import java.net.ProtocolException
import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicReference

class TodoRestApi(private val todoRestClient: TodoRestClient) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun waitForGet(responseMatcher: Matcher<Any>): List<TodoEntity> {
        val result = AtomicReference<List<TodoEntity>>()
        Awaitility.await()
                .failFast("Problem into the tests or environment configuration", FailFastCallable(log, todoRestClient))
                .until({
                    val todoEntityList = todoRestClient.get()
                    result.set(todoEntityList)
                    return@until result.get()
                }, `is`(responseMatcher))
        return result.get()
    }

    fun get(offset: Long, limit: Long): List<TodoEntity> {
        TODO("Not yet implemented")
    }

    fun getWithOffset(offset: Long): List<TodoEntity> {
        TODO("Not yet implemented")
    }

    fun getWithLimit(limit: Long): List<TodoEntity> {
        TODO("Not yet implemented")
    }

    fun post(todoEntity: TodoEntity): Any {
        TODO("Not yet implemented")
    }

    fun put(todoEntity: TodoEntity): Any {
        TODO("Not yet implemented")
    }

    fun delete(todoEntityId: Any): Any {
        TODO("Not yet implemented")
    }

}

class FailFastCallable(private val log: Logger, private val todoRestClient: TodoRestClient) : Callable<Boolean> {

    override fun call(): Boolean {
        try {
            todoRestClient.get()
            return false
        } catch (e: Exception) {
            val rootCause = ExceptionUtils.getRootCause(e)
            log.warn("Something went wrong", rootCause)
            return when (rootCause) {
                is ProtocolException -> true
                is ConnectionClosedException -> true
                is MessageConstraintException -> true
                is MalformedChunkCodingException -> true
                is MethodNotSupportedException -> true
                is NoHttpResponseException -> true
                is ParseException -> true
                is UnsupportedHttpVersionException -> true
                else -> false
            }
        }
    }

}
