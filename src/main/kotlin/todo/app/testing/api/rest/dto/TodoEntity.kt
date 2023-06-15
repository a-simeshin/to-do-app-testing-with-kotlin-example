package todo.app.testing.api.rest.dto

/** DTO for main entity used in RestApi for testing */
data class TodoEntity(val id: Long, val text: String, val completed: Boolean)
