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
@EnableConfigurationProperties(TodoRestClientConfigData::class)
class TodoRestClientConfiguration {

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

    @PostConstruct
    fun initAwaitility() {
        Awaitility.pollInSameThread()
        Awaitility.ignoreExceptionsByDefault()
        Awaitility.setDefaultPollDelay(500, TimeUnit.MILLISECONDS)
        Awaitility.setDefaultTimeout(10, TimeUnit.SECONDS)
    }
}
