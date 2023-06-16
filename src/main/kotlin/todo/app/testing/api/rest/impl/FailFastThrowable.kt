package todo.app.testing.api.rest.impl

import org.apache.commons.lang3.exception.ExceptionUtils
import org.awaitility.core.ThrowingRunnable

class FailFastThrowable(private val todoRestClient: TodoRestClient) : ThrowingRunnable {

    private fun isTestsBroken(e: Throwable): Boolean {
        return when (e) {
            is java.net.ProtocolException -> true
            is javax.net.ssl.SSLException -> true
            is org.apache.hc.core5.http.ProtocolException -> true
            is org.apache.hc.client5.http.UnsupportedSchemeException -> true
            else -> false
        }
    }

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
