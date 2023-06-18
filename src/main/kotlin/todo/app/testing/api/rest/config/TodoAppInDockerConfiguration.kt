package todo.app.testing.api.rest.config

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer

@Configuration
@EnableConfigurationProperties(TodoAppInDockerConfigData::class)
class TodoAppInDockerConfiguration {

    @Bean
    @ConditionalOnProperty(
        prefix = "to-do-app-in-docker",
        name = ["enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun todoAppInDockerContainer(dockerProperties: TodoAppInDockerConfigData): GenericContainer<*> {
        val genericContainer = GenericContainer(dockerProperties.imageName)
        dockerProperties.exposedPorts.forEach { (hostPort, containerPort) ->
            genericContainer.addExposedPort(hostPort)
            genericContainer.portBindings.add("$hostPort:$containerPort")
        }
        if (dockerProperties.logging) {
            genericContainer.logConsumers =
                listOf(Slf4jLogConsumer(LoggerFactory.getLogger(this.javaClass)))
        }
        return genericContainer
    }
}
