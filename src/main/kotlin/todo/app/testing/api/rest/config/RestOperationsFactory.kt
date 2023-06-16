package todo.app.testing.api.rest.config

import io.qameta.allure.springweb.AllureRestTemplate
import java.nio.file.Paths
import java.util.Optional
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.pem.util.PemUtils
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.socket.ConnectionSocketFactory
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.core5.http.config.Registry
import org.apache.hc.core5.http.config.RegistryBuilder
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestOperations
import org.springframework.web.util.DefaultUriBuilderFactory

/**
 * A factory for creating RestOperations instances based on the provided TodoRestClientConfigData
 * settings.
 *
 * <p>The RestOperationsFactory takes in a TodoRestClientConfigData object which holds the necessary
 * settings for the construction of the RestOperations instance.</p>
 *
 * <p>The primary function provided by this class, buildRestOperations(), sets up a
 * RestTemplateBuilder with AllureRestTemplate interceptors for Allure reporting, sets up a request
 * factory configured to support both HTTP and HTTPS connections (based on the SSLContext built
 * using the provided settings), and sets up a DefaultUriBuilderFactory with the base URL from the
 * settings.</p>
 *
 * @property settings The configuration settings to be used in the construction of the
 * RestOperations instance.
 * @constructor Creates a RestOperationsFactory with the provided configuration settings.
 */
class RestOperationsFactory(private val settings: TodoRestClientConfigData) {

    /**
     * Builds a RestOperations instance with specific configurations.
     *
     * <p>The function sets up a RestTemplateBuilder with AllureRestTemplate interceptors for Allure
     * reporting.</p>
     *
     * <p>The request factory is configured to support both HTTP and HTTPS connections. The
     * SSLContext is built and used to create an SSLConnectionSocketFactory, which is used to create
     * a CloseableHttpClient. This CloseableHttpClient is then used to create a
     * BufferingClientHttpRequestFactory, which buffers output and input streams to allow for
     * multiple reads of the response. This request factory is then set for the
     * RestTemplateBuilder.</p>
     *
     * <p>A DefaultUriBuilderFactory is set up with the base URL from the settings and set as the
     * uriTemplateHandler for the RestTemplateBuilder.</p>
     *
     * @return A RestOperations instance with the configured settings.
     */
    fun buildRestOperations(): RestOperations {
        // Probably this builder should be autowired in case of metrics or spring autoconfigured
        // interceptors
        val restTemplateBuilder = RestTemplateBuilder()
        val build = restTemplateBuilder.build()

        val optionalSslContext = buildSslContext()
        val optionalSslConnectionSocketFactory =
            createSslConnectionSocketFactory(optionalSslContext)
        val closableHttpClient = createClosableHttpClient(optionalSslConnectionSocketFactory)

        val httpComponentsClientHttpRequestFactory = HttpComponentsClientHttpRequestFactory()
        httpComponentsClientHttpRequestFactory.httpClient = closableHttpClient
        httpComponentsClientHttpRequestFactory.setConnectTimeout(settings.connectTimeout)
        httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(
            settings.connectionRequestTimeout
        )

        // Add http and https both support, factory should buffer responses to access in
        // AllureRestTemplate
        build.requestFactory =
            BufferingClientHttpRequestFactory(httpComponentsClientHttpRequestFactory)

        // Add default uri predicate to all requests
        build.uriTemplateHandler = DefaultUriBuilderFactory(settings.url)

        // Add attachments for allure report
        if (settings.enableHttpAttachments) {
            build.interceptors.add(AllureRestTemplate())
        }
        return build
    }

    /**
     * Builds the SSLContext based on the provided settings. This function constructs the SSLContext
     * needed for secure communication in the RestOperations API.
     *
     * <p>If the 'ssl' setting is not enabled, it will return an empty optional.</p>
     *
     * <p>If keyStorePath and keyStorePassword are provided in the settings, they are used to build
     * the identity material for the SSLFactory. If not provided, the certificateChain and
     * privateKey settings are used to load the identity material from the PEM files.</p>
     *
     * <p>Similar logic is applied for trustStore - if trustStorePath and trustStorePassword are
     * provided, they are used to build the trust material for the SSLFactory. If not provided, the
     * trustCertCollectionPath setting is used to load the trust material from the PEM file.</p>
     *
     * @return An Optional of SSLContext, the SSLContext if SSL is enabled and settings are properly
     * set, Optional.empty() otherwise.
     */
    private fun buildSslContext(): Optional<SSLContext> {
        if (!settings.ssl) return Optional.empty()

        val sslBuilder = SSLFactory.builder()

        // Build KeyStore
        if (settings.keyStorePath.isNotEmpty() && settings.keyStorePassword.isNotEmpty()) {
            sslBuilder.withIdentityMaterial(
                Paths.get(settings.keyStorePath),
                settings.keyStorePassword.toCharArray()
            )
        } else {
            sslBuilder.withIdentityMaterial(
                PemUtils.loadIdentityMaterial(
                    Paths.get(settings.certificateChain),
                    Paths.get(settings.privateKey)
                )
            )
        }

        // Build TrustStore
        if (settings.trustStorePath.isNotEmpty() && settings.trustStorePassword.isNotEmpty()) {
            sslBuilder.withTrustMaterial(
                Paths.get(settings.trustStorePath),
                settings.trustStorePassword.toCharArray()
            )
        } else {
            sslBuilder.withTrustMaterial(
                PemUtils.loadTrustMaterial(Paths.get(settings.trustCertCollectionPath))
            )
        }

        return Optional.of(sslBuilder.build().sslContext)
    }

    /**
     * Creates an Optional SSLConnectionSocketFactory given an Optional SSLContext.
     *
     * <p>If the provided Optional SSLContext is empty, this function will return an empty
     * Optional.</p>
     *
     * <p>If the SSLContext is present, this function creates a SSLConnectionSocketFactory with the
     * provided SSLContext and an instance of AllowAllHostnameVerifier.</p>
     *
     * <p>AllowAllHostnameVerifier is a type of HostnameVerifier that allows all hostnames, which
     * might be appropriate in certain testing or non-production scenarios. Please be aware that
     * this can expose your application to security risks in a production environment.</p>
     *
     * @param context An Optional SSLContext to be used for creating the SSLConnectionSocketFactory,
     * if present.
     * @return An Optional SSLConnectionSocketFactory created with the provided SSLContext, or an
     * empty Optional if the SSLContext was not present.
     */
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun createSslConnectionSocketFactory(
        context: Optional<SSLContext>
    ): Optional<SSLConnectionSocketFactory> {
        if (context.isEmpty) return Optional.empty()

        @Suppress("RedundantSamConstructor")
        return Optional.of(
            SSLConnectionSocketFactory(
                context.get(),
                HostnameVerifier { hostname, session -> true }
            )
        )
    }

    /**
     * Creates a Registry of ConnectionSocketFactory with both HTTP and HTTPS support.
     *
     * <p>The Registry will always include a PlainConnectionSocketFactory for HTTP connections.</p>
     *
     * <p>If an SSLConnectionSocketFactory is provided, it will be registered to handle HTTPS
     * connections. If the Optional SSLConnectionSocketFactory is not present, the Registry will not
     * have support for HTTPS connections.</p>
     *
     * @param sslConnectionSocketFactory An Optional containing the SSLConnectionSocketFactory to be
     * used for HTTPS connections, if present.
     * @return A Registry of ConnectionSocketFactory configured with the provided
     * SSLConnectionSocketFactory (if present).
     */
    private fun createRegistry(
        sslConnectionSocketFactory: Optional<SSLConnectionSocketFactory>
    ): Registry<ConnectionSocketFactory> {
        val builder = RegistryBuilder.create<ConnectionSocketFactory>()
        builder.register("http", PlainConnectionSocketFactory())
        if (sslConnectionSocketFactory.isPresent) {
            builder.register("https", sslConnectionSocketFactory.get())
        }
        return builder.build()
    }

    /**
     * Creates a CloseableHttpClient with the given Optional SSLConnectionSocketFactory.
     *
     * <p>This method initializes a PoolingHttpClientConnectionManager with a
     * ConnectionSocketFactory Registry created with the given SSLConnectionSocketFactory. The
     * connection manager is then configured to allow a maximum of connections defined by the
     * 'maxTotalConnections' setting and a default maximum of connections per route defined by the
     * 'maxConnectionsPerRoute' setting.</p>
     *
     * <p>A custom HttpClients builder is used to set the connection manager and the default request
     * configuration. The default request configuration includes a connection timeout, a connection
     * request timeout, and a socket timeout, all set to the values defined in the settings.</p>
     *
     * <p>If the SSLConnectionSocketFactory is present, it is set as the SSL Socket Factory for the
     * HttpClients builder.</p>
     *
     * @param sslConnectionSocketFactory An Optional containing the SSLConnectionSocketFactory to be
     * used for HTTPS connections, if present.
     * @return A CloseableHttpClient configured with the provided SSLConnectionSocketFactory (if
     * present).
     */
    private fun createClosableHttpClient(
        sslConnectionSocketFactory: Optional<SSLConnectionSocketFactory>
    ): CloseableHttpClient {
        val httpRegistry = createRegistry(sslConnectionSocketFactory)
        val connectionManager = PoolingHttpClientConnectionManager(httpRegistry)
        connectionManager.maxTotal = settings.maxTotalConnections
        connectionManager.defaultMaxPerRoute = settings.maxConnectionsPerRoute
        connectionManager.setDefaultConnectionConfig(
            ConnectionConfig.custom()
                .setSocketTimeout(settings.socketTimeout, TimeUnit.MILLISECONDS)
                .setConnectTimeout(settings.connectTimeout.toLong(), TimeUnit.MILLISECONDS)
                .build()
        )

        val clientBuilder =
            HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(
                    RequestConfig.custom()
                        .setConnectionRequestTimeout(
                            settings.connectionRequestTimeout.toLong(),
                            TimeUnit.MILLISECONDS
                        )
                        .build()
                )
        return clientBuilder.build()
    }
}
