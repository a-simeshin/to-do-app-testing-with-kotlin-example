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

    /**
     * Retrieves a list of `TodoEntity` objects using the GET method.
     *
     * @return The list of `TodoEntity` objects obtained from the server.
     * @throws NullPointerException if the response body is null.
     */
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

    /**
     * Retrieves a list of `TodoEntity` objects using the GET method with optional offset and limit
     * parameters.
     *
     * @param offset The offset value for pagination, or null if not specified.
     * @param limit The limit value for pagination, or null if not specified.
     * @return The list of `TodoEntity` objects obtained from the server based on the specified
     * offset and limit.
     * @throws NullPointerException if the response body is null.
     */
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

    /**
     * Retrieves a list of `TodoEntity` objects using the GET method with the specified offset
     * parameter.
     *
     * @param offset The offset value for pagination, or null if not specified.
     * @return The list of `TodoEntity` objects obtained from the server based on the specified
     * offset.
     * @throws NullPointerException if the response body is null.
     */
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

    /**
     * Retrieves a list of `TodoEntity` objects using the GET method with the specified limit
     * parameter.
     *
     * @param limit The limit value for pagination, or null if not specified.
     * @return The list of `TodoEntity` objects obtained from the server based on the specified
     * limit.
     * @throws NullPointerException if the response body is null.
     */
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

    /**
     * Creates a new TodoEntity by sending a POST request to the server.
     *
     * @param todoEntity The TodoEntity object to be posted.
     * @throws AssertionError if the HTTP status code of the response is not 201 (CREATED).
     */
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

    /**
     * Updates an existing TodoEntity by sending a PUT request to the server.
     *
     * @param todoEntity The TodoEntity object to be updated.
     * @throws AssertionError if the HTTP status code of the response is not 200 (OK).
     */
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

    /**
     * Deletes a TodoEntity by sending a DELETE request to the server.
     *
     * @param todoEntityId The ID of the TodoEntity to be deleted.
     * @throws AssertionError if the HTTP status code of the response is neither 204 (NO_CONTENT)
     * nor 404 (NOT_FOUND).
     */
    fun delete(todoEntityId: Any?) {
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

    /**
     * Retrieves the HttpHeaders object with the necessary headers for making a request.
     *
     * @return The HttpHeaders object with the required headers.
     */
    private fun getHeaders(): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.accept = singletonList(MediaType.APPLICATION_JSON)
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        return httpHeaders
    }
}
