package com.arrow.issues

import arrow.core.Either
import arrow.fx.coroutines.IOPool
import arrow.fx.coroutines.evalOn
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpMethod.Companion.Get
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.net.ConnectException

internal class KtorClientTest {

    private val httpClient: HttpClient = HttpClient(CIO) {
        followRedirects = false
    }

    private suspend inline fun <reified A> execute(request: HttpRequestBuilder): Either<Throwable, A> {
        return evalOn(IOPool) { Either.catch { httpClient.request<A>(request) } }
    }

    @Test
    fun `this test works when executed in intellij`(): Unit = runBlocking {
        val request: HttpRequestBuilder = HttpRequestBuilder()
            .apply {
                url("http://localhost:8080/fav.ico")
                this.method = Get
            }

        shouldThrow<ConnectException> { httpClient.request<String>(request) }
    }

    @Test
    fun `this test with eval hangs when executed in intellij or gradle`(): Unit = runBlocking {
        val request: HttpRequestBuilder = HttpRequestBuilder()
            .apply {
                url("http://localhost:8080/fav.ico")
                this.method = Get
            }

        shouldThrow<ConnectException> { evalOn(IOPool) { httpClient.request<String>(request) } }
    }

    @Test
    fun `this test hangs when executed in intellij or gradle`(): Unit = runBlocking {
        val request: HttpRequestBuilder = HttpRequestBuilder()
            .apply {
                url("http://localhost:8080/fav.ico")
                this.method = Get
            }

        val result: Either<Throwable, String> = execute(request)

        result.isLeft() shouldBe false
    }
}
