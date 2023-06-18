package todo.app.testing.api.rest.impl

import java.util.Collections.singletonList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.client.RestOperations
import todo.app.testing.api.rest.config.TodoRestClientConfigData
import todo.app.testing.api.rest.dto.TodoEntity

class TodoRestClient(
    private val restOperations: RestOperations,
    private val setting: TodoRestClientConfigData
) {

    private inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

    fun get(): List<TodoEntity> {
        return restOperations
            .exchange(
                setting.todosGetPath,
                HttpMethod.GET,
                HttpEntity<Any>(getHeaders()),
                typeReference<List<TodoEntity>>()
            )
            .body
            ?: throw NullPointerException("Got null from rest GET " + setting.todosGetPath)
    }

    fun get(offset: Any?, limit: Any?): List<TodoEntity> {
        val newUrl = setting.todosGetPath + "?offset=" + offset + "&limit=" + limit
        return restOperations
            .exchange(
                newUrl,
                HttpMethod.GET,
                HttpEntity<Any>(getHeaders()),
                typeReference<List<TodoEntity>>()
            )
            .body
            ?: throw NullPointerException("Got null from GET $newUrl")
    }

    fun getWithOffset(offset: Any?): List<TodoEntity> {
        val newUrl = setting.todosGetPath + "?offset=" + offset
        return restOperations
            .exchange(
                newUrl,
                HttpMethod.GET,
                HttpEntity<Any>(getHeaders()),
                typeReference<List<TodoEntity>>()
            )
            .body
            ?: throw NullPointerException("Got null from GET $newUrl")
    }

    fun getWithLimit(limit: Any?): List<TodoEntity> {
        val newUrl = setting.todosGetPath + "?limit=" + limit
        return restOperations
            .exchange(
                newUrl,
                HttpMethod.GET,
                HttpEntity<Any>(getHeaders()),
                typeReference<List<TodoEntity>>()
            )
            .body
            ?: throw NullPointerException("Got null from GET $newUrl")
    }

    fun post(todoEntity: Any?) {
        val exchange =
            restOperations.exchange(
                setting.todosPostPath,
                HttpMethod.POST,
                HttpEntity<Any>(todoEntity, getHeaders()),
                typeReference<String>()
            )
        assertThat(
            "Создание нового TODO завершается кодом 201 - CREATED",
            exchange.statusCode,
            `is`(HttpStatus.CREATED)
        )
    }

    fun put(todoEntity: TodoEntity) {
        val exchange =
            restOperations.exchange(
                setting.todosPutPath,
                HttpMethod.PUT,
                HttpEntity<TodoEntity>(todoEntity, getHeaders()),
                typeReference<String>(),
                todoEntity.id
            )
        assertThat(
            "Обновление TODO завершилось кодом 200 - OK",
            exchange.statusCode,
            `is`(HttpStatus.OK)
        )
    }

    fun delete(todoEntityId: Any) {
        val headers = getHeaders()
        headers.setBasicAuth(setting.basicAuthLogin, setting.basicAuthPassword)
        val exchange =
            restOperations.exchange(
                setting.todosDeletePath,
                HttpMethod.DELETE,
                HttpEntity<Any>(headers),
                typeReference<String>(),
                todoEntityId
            )
        assertThat(
            "Удаление TODO завершилось кодом 204 - NO_CONTENT или кодом 404 - NOT_FOUND",
            exchange.statusCode,
            anyOf(`is`(HttpStatus.NO_CONTENT), `is`(HttpStatus.NOT_FOUND))
        )
    }

    private fun getHeaders(): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.accept = singletonList(MediaType.APPLICATION_JSON)
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        return httpHeaders
    }
}
