package todo.app.testing.api.rest.impl

import org.springframework.web.client.RestOperations
import todo.app.testing.api.rest.config.TodoRestClientConfigData
import todo.app.testing.api.rest.dto.TodoEntity

class TodoRestClient
constructor(
    private val restOperations: RestOperations,
    private val setting: TodoRestClientConfigData
) {

    fun get(offset: Any, limit: Any): List<TodoEntity> {
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
