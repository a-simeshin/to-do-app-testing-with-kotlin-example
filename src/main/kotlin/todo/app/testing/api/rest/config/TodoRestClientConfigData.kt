package todo.app.testing.api.rest.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Data class used to configure the REST client for testing purposes. This class holds various
 * properties used for creating a RestTemplate based HttpClient, such as URL paths, SSL settings,
 * and keystores/truststores paths and passwords.
 *
 * <p>This class contains obligate parameters such as url and different API paths, and optional
 * parameters for configuring SSL, key stores, and trust stores, both in JKS and PEM formats.</p>
 *
 * <p>Note: You need to set either JKS or PEM parameters based on the SSL configuration of the
 * server.</p>
 *
 * <p>JKS and PEM format are used for different purposes. JKS format is a keystore format used by
 * Java applications to bundle a private key and its certificate chain (including its own
 * certificate and the certificate authority's certificate) into a single file. PEM format is
 * commonly used for certificate files such as .pem, .crt, .cer and .key files, it is a more
 * flexible format that can include all the certificates and the private key in one file.</p>
 *
 */
@ConfigurationProperties(prefix = "rest-client-config-data")
data class TodoRestClientConfigData(

    // obligate parameters
    val url: String = "http://localhost:8080",
    val todosGetPath: String = "/todos",
    val todosPostPath: String = "/todos/{id}",
    val todosPutPath: String = "/todos/{id}",
    val todosDeletePath: String = "/todos/{id}",

    // SSL Configuration
    val ssl: Boolean = false,

    // for JKS stores format
    val keyStorePath: String = "",
    val keyStorePassword: String = "",
    val trustStorePath: String = "",
    val trustStorePassword: String = "",

    // for PEM stores format
    // Trusting a Servers
    val trustCertCollectionPath: String = "",
    // Mutual Certificate Authentication
    val certificateChain: String = "",
    val privateKey: String = "",

    // HttpClient configuration
    val connectTimeout: Int = 30_000,
    val connectionRequestTimeout: Int = 30_000,
    val socketTimeout: Int = 30_000,
    val maxTotalConnections: Int = 1000,
    val maxConnectionsPerRoute: Int = 5,

    //Allure interceptor configuration
    val enableHttpAttachments: Boolean = true
) {
    fun validate(): TodoRestClientConfigData {
        if (url.isEmpty()) {
            throw IllegalArgumentException("url must not be null or empty")
        }
        if (todosGetPath.isEmpty()) {
            throw IllegalArgumentException("todosGetPath must not be null or empty")
        }
        if (todosPostPath.isEmpty()) {
            throw IllegalArgumentException("todosPostPath must not be null or empty")
        }
        if (todosPutPath.isEmpty()) {
            throw IllegalArgumentException("todosPutPath must not be null or empty")
        }
        if (todosDeletePath.isEmpty()) {
            throw IllegalArgumentException("todosDeletePath must not be null or empty")
        }

        if (!ssl) return this

        if (keyStorePath.isEmpty() &&
                trustStorePath.isEmpty() &&
                certificateChain.isEmpty() &&
                trustCertCollectionPath.isEmpty()
        ) {
            throw IllegalArgumentException(
                "SSL is enabled, but not provided any of trust store or certificate chain or trust store collection path"
            )
        }
        if (keyStorePath.isNotEmpty() && keyStorePassword.isEmpty()) {
            throw IllegalArgumentException(
                "keyStorePassword must not be null or empty if keyStorePath file filled"
            )
        }
        if (trustStorePath.isNotEmpty() && trustStorePassword.isEmpty()) {
            throw IllegalArgumentException(
                "trustStorePassword must not be null or empty if trustStorePath file filled"
            )
        }
        if (certificateChain.isNotEmpty() && privateKey.isEmpty()) {
            throw IllegalArgumentException(
                "privateKey must be provides as valid path for file if certificateChain file filled"
            )
        }

        return this
    }
}
