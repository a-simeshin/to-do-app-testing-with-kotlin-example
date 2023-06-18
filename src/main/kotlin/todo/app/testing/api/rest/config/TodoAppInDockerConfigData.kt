package todo.app.testing.api.rest.config

import org.springframework.boot.context.properties.ConfigurationProperties

/** Test-containers property bean for autoconfiguration with prefix = to-do-app-in-docker */
@ConfigurationProperties(prefix = "to-do-app-in-docker")
data class TodoAppInDockerConfigData(
    val enabled: Boolean = false,
    val logging: Boolean = true,
    val imageName: String = "todo-app:latest",
    val exposedPorts: Map<Int, Int> = mapOf(8080 to 4242)
)
