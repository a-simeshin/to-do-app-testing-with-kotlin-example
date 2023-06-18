package todo.app.testing.api.rest.config

import org.springframework.boot.context.properties.ConfigurationProperties

/** Awaitility property bean for autoconfiguration with prefix = awaitility */
@ConfigurationProperties(prefix = "awaitility")
data class AwaitilityConfigData(
    val defaultPollInterval: Long = 500,
    val defaultTimeout: Long = 10000
)
