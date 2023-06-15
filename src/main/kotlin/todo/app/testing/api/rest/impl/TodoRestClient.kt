package todo.app.testing.api.rest.impl

import io.qameta.allure.Step
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestOperations
import todo.app.testing.api.rest.config.TodoRestClientConfigData
import todo.app.testing.api.rest.dto.TodoEntity
import java.util.Collections.singletonList

class TodoRestClient(
        private val restOperations: RestOperations,
        private val setting: TodoRestClientConfigData
) {

    private inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

    @Step("GET todo list entities")
    fun get(): List<TodoEntity> {
        return restOperations.exchange(
                setting.todosGetPath,
                HttpMethod.GET,
                HttpEntity<Any>(getHeaders()),
                typeReference<List<TodoEntity>>()
        ).body ?: throw NullPointerException("Got null from rest GET " + setting.todosGetPath)
    }

    @Step("GET todo list entities with offset and limit")
    fun get(offset: Long, limit: Long): List<TodoEntity> {
        val newUrl = setting.todosGetPath + "?offset=" + offset + "&limit=" + limit
        return restOperations.exchange(
                newUrl,
                HttpMethod.GET,
                HttpEntity<Any>(getHeaders()),
                typeReference<List<TodoEntity>>()
        ).body ?: throw NullPointerException("Got null from GET $newUrl")
    }

    @Step("GET todo list entities with offset")
    fun getWithOffset(offset: Long): List<TodoEntity> {
        val newUrl = setting.todosGetPath + "?offset=" + offset
        return restOperations.exchange(
                newUrl,
                HttpMethod.GET,
                HttpEntity<Any>(getHeaders()),
                typeReference<List<TodoEntity>>()
        ).body ?: throw NullPointerException("Got null from GET $newUrl")
    }

    @Step("GET todo list entities with limit")
    fun getWithLimit(limit: Long): List<TodoEntity> {
        val newUrl = setting.todosGetPath + "?limit=" + limit
        return restOperations.exchange(
                newUrl,
                HttpMethod.GET,
                HttpEntity<Any>(getHeaders()),
                typeReference<List<TodoEntity>>()
        ).body ?: throw NullPointerException("Got null from GET $newUrl")
    }

    @Step("POST todo entity")
    fun post(todoEntity: TodoEntity): Any {
        return restOperations.exchange(
                setting.todosPostPath + "/" + todoEntity.id,
                HttpMethod.GET,
                HttpEntity<TodoEntity>(todoEntity, getHeaders()),
                typeReference<String>()
        ).body ?: throw NullPointerException("Got null from POST " + setting.todosPostPath)
    }

    @Step("PUT todo entity")
    fun put(todoEntity: TodoEntity): Any {
        return restOperations.exchange(
                setting.todosPostPath + "/" + todoEntity.id,
                HttpMethod.PUT,
                HttpEntity<TodoEntity>(todoEntity, getHeaders()),
                typeReference<String>()
        ).body ?: throw NullPointerException("Got null from PUT " + setting.todosPostPath)
    }

    @Step("DELETE todo entity by id")
    fun delete(todoEntityId: Long): Any {
        return restOperations.exchange(
                setting.todosPostPath + "/" + todoEntityId,
                HttpMethod.DELETE,
                HttpEntity<TodoEntity>(getHeaders()),
                typeReference<String>()
        ).body ?: throw NullPointerException("Got null from DELETE " + setting.todosPostPath)
    }

    private fun getHeaders(): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.accept = singletonList(MediaType.APPLICATION_JSON)
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        return httpHeaders
    }
}
