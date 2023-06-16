package todo.app.testing.api.rest.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "to-do-app-in-docker")
data class TodoAppInDockerProperties(
    val enabled: Boolean = false,
    val imageName: String = "todo-app:latest",
    val exposedPorts: Map<Int, Int> = mapOf(8080 to 4242)
)
