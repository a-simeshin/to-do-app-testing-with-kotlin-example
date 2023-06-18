package todo.app.testing.api.rest.config

import jakarta.annotation.PostConstruct
import java.util.concurrent.TimeUnit
import org.awaitility.Awaitility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations
import todo.app.testing.api.rest.impl.TodoRestApi
import todo.app.testing.api.rest.impl.TodoRestClient

/**
 * Configuration class for TodoRestClient. This class is responsible for configuring and creating
 * the TodoRestClient bean using the provided RestOperations and TodoRestClientConfigData.
 */
@Configuration
@EnableConfigurationProperties(
    value = [TodoRestClientConfigData::class, AwaitilityConfigData::class]
)
class TodoRestClientConfiguration {

    /**
     * Creates and configures a TodoRestApi instance.
     *
     * This method is annotated with `@Bean`, indicating that it should be treated as a bean
     * definition and managed by the Spring framework.
     *
     * @param todoRestClient The TodoRestClient dependency to be injected into the TodoRestApi.
     * @return A TodoRestApi instance with the specified TodoRestClient dependency.
     */
    @Bean
    fun todoRestApi(@Autowired todoRestClient: TodoRestClient): TodoRestApi {
        return TodoRestApi(todoRestClient)
    }

    /**
     * Creates a TodoRestClient bean.
     *
     * @param restOperations The RestOperations bean to be used by the TodoRestClient.
     * @param todoRestClientConfigData The TodoRestClientConfigData bean containing the
     * configuration data.
     * @return Created TodoRestClient instance.
     */
    @Bean
    fun todoRestClient(
        @Autowired restOperations: RestOperations,
        @Autowired todoRestClientConfigData: TodoRestClientConfigData
    ): TodoRestClient {
        return TodoRestClient(restOperations, todoRestClientConfigData)
    }

    /**
     * Creates a RestOperations bean.
     * @param settings The TodoRestClientConfigData bean containing the configuration data.
     * @return The created RestOperations instance.
     */
    @Bean
    fun RestOperations(@Autowired settings: TodoRestClientConfigData): RestOperations {
        return RestOperationsFactory(settings.validate()).buildRestOperations()
    }

    @Autowired lateinit var awaitilityConfigData: AwaitilityConfigData

    /**
     * Initializes Awaitility with the specified configuration.
     *
     * This method is annotated with `@PostConstruct`, indicating that it should be invoked
     * automatically after the bean is constructed and its dependencies are injected.
     *
     * This method configures Awaitility with the following settings:
     * - Polling in the same thread.
     * - Ignoring exceptions by default.
     * - Setting the default poll interval to the value specified in
     * `awaitilityConfigData.defaultPollInterval`, with a time unit of milliseconds.
     * - Setting the default timeout to the value specified in
     * `awaitilityConfigData.defaultTimeout`, with a time unit of milliseconds.
     */
    @PostConstruct
    fun initAwaitility() {
        Awaitility.pollInSameThread()
        Awaitility.ignoreExceptionsByDefault()
        Awaitility.setDefaultPollInterval(
            awaitilityConfigData.defaultPollInterval,
            TimeUnit.MILLISECONDS
        )
        Awaitility.setDefaultTimeout(awaitilityConfigData.defaultTimeout, TimeUnit.MILLISECONDS)
    }
}
