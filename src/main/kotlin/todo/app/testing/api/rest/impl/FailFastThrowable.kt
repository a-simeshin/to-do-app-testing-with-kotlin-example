package todo.app.testing.api.rest.impl

import org.apache.commons.lang3.exception.ExceptionUtils
import org.awaitility.core.ThrowingRunnable

class FailFastThrowable(private val todoRestClient: TodoRestClient) : ThrowingRunnable {

    /**
     * Checks if the given exception indicates that the tests are broken.
     *
     * This method is used to determine if a particular exception should be considered as an
     * indicator that the tests are broken. It returns `true` if the exception matches any of the
     * following types:
     * - `java.net.ProtocolException`
     * - `javax.net.ssl.SSLException`
     * - `org.apache.hc.core5.http.ProtocolException`
     * - `org.apache.hc.client5.http.UnsupportedSchemeException`
     *
     * @param e The Throwable instance to check.
     * @return `true` if the exception indicates that the tests are broken, `false` otherwise.
     */
    private fun isTestsBroken(e: Throwable): Boolean {
        return when (e) {
            is java.net.ProtocolException -> true
            is javax.net.ssl.SSLException -> true
            is org.apache.hc.core5.http.ProtocolException -> true
            is org.apache.hc.client5.http.UnsupportedSchemeException -> true
            else -> false
        }
    }

    /**
     * Checks if the given exception indicates that the environment is broken.
     *
     * This method is used to determine if a particular exception should be considered as an
     * indicator that the environment is broken. It returns `true` if the exception matches any of
     * the following types:
     * - `java.net.ConnectException` (excluding cases where the exception message contains
     * "Connection refused")
     * - `java.net.SocketException`
     * - `org.apache.hc.client5.http.ConnectTimeoutException`
     * - `org.apache.hc.core5.http.ConnectionRequestTimeoutException`
     * - `org.apache.hc.core5.http.ConnectionClosedException`
     * - `org.apache.hc.core5.http.MethodNotSupportedException`
     * - `org.apache.hc.core5.http.NoHttpResponseException`
     *
     * @param e The Throwable instance to check.
     * @return `true` if the exception indicates that the environment is broken, `false` otherwise.
     */
    private fun isEnvironmentBroken(e: Throwable): Boolean {
        return when (e) {
            is java.net.ConnectException -> {
                // Exception when application is deployed but still in the process of starting or
                // restarting, will have Connection refused result
                return !(e.message!!.contains("Connection refused"))
            }
            is java.net.SocketException -> true
            is org.apache.hc.client5.http.ConnectTimeoutException -> true
            is org.apache.hc.core5.http.ConnectionRequestTimeoutException -> true
            is org.apache.hc.core5.http.ConnectionClosedException -> true
            is org.apache.hc.core5.http.MethodNotSupportedException -> true
            is org.apache.hc.core5.http.NoHttpResponseException -> true
            else -> false
        }
    }

    /**
     * Executes the test logic.
     *
     * This method is called when the test is run. It invokes the `get()` method of the
     * `todoRestClient` to perform a test operation. If an exception occurs during the test, this
     * method analyzes the root cause to determine if the environment or the tests themselves are
     * broken. It throws a `RuntimeException` with an appropriate error message and the root cause
     * exception if either case is detected.
     *
     * @throws RuntimeException If there is an issue with the tests environment or configuration.
     */
    override fun run() {
        try {
            todoRestClient.get()
        } catch (e: Exception) {
            val rootCause = ExceptionUtils.getRootCause(e)
            if (isEnvironmentBroken(rootCause)) {
                throw RuntimeException(
                    "Something went wrong with tests environment stand or application",
                    rootCause
                )
            }
            if (isTestsBroken(rootCause)) {
                throw RuntimeException("Something went wrong with tests configuration", rootCause)
            }
        }
    }
}
