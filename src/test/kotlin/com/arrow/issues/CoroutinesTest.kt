package com.arrow.issues

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.right
import arrow.fx.coroutines.ComputationPool
import arrow.fx.coroutines.Environment
import arrow.fx.coroutines.evalOn
import arrow.fx.coroutines.parTupledN
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.slf4j.MDCContextMap
import org.junit.jupiter.api.Test
import org.slf4j.MDC

internal class CoroutinesTest {

    private val key = "key"
    private suspend fun getThreadName(): String = Thread.currentThread().name

    // for mdc refer : https://github.com/Kotlin/kotlinx.coroutines/tree/master/integration/kotlinx-coroutines-slf4j
    @Test
    fun `it just prints the different threads the cursor is in`() {
        MDC.put(key, "myValue") // 👍 : Correct behavior

        println("in test, $key: ${MDC.get(key)}") // 👍 : Correct behavior

        runBlocking(MDCContext()) {
            println("I am in ${getThreadName()}") // 👍 : Correct behavior

            println("in runBlocking, $key: ${MDC.get(key)}") // 👍 : Correct behavior

            parTupledN(
                ComputationPool + MDCContext(),
                suspend {
                    println("I am in parTupledN, ${getThreadName()}") // 👍 : Correct behavior

                    println("in parTupledN block, $key: ${MDC.get(key)}") // 👎 : expected MDC value
                },
                suspend {
                    println("I am in parTupledN, ${getThreadName()}") // 👍 : Correct behavior

                    println("in parTupledN block, $key: ${MDC.get(key)}") // 👎 : expected MDC value
                }
            )

            val result: Either<Throwable, String> = either {
                println("I am in ${getThreadName()}") // 👍 : Correct behavior

                println("in either block, $key: ${MDC.get(key)}") // 👎 : expected MDC value

                val threadName: String = evalOn(ComputationPool + MDCContext()) {
                    println("I am in ${getThreadName()}") // 👍 : Correct behavior

                    println("in evalOn block, $key: ${MDC.get(key)}") // 👎 : expected MDC value

                    getThreadName().right()
                }.bind()

                println("Evaluated thread result : $threadName in ${getThreadName()}") // 👎 : it should print `in thread name` on line #47

                println("in either block after evalOn, $key: ${MDC.get(key)}") // 👎 : expected MDC value

                parTupledN(
                    ComputationPool + MDCContext(),
                    suspend {
                        println("I am in ${getThreadName()}") // 👍 : Correct behavior

                        println("in parTupledN block after evalOn, $key: ${MDC.get(key)}") // 👎 : expected MDC value
                    },
                    suspend {
                        println("I am in ${getThreadName()}") // 👍 : Correct behavior

                        println("in parTupledN block after evalOn, $key: ${MDC.get(key)}") // 👎 : expected MDC value
                    }
                )

                threadName
            }

            println("Evaluated either block result : ${result.orNull()} in ${getThreadName()}") // 👎 : it should print `in thread name` on line #28

            println("in mdcContext after either block, $key: ${MDC.get(key)}") // 👎 : expected MDC value
        }

        println("in test, $key: ${MDC.get(key)}") // 👍 : Correct behavior
    }

    @Test
    fun `it just prints the different threads the cursor is in with arrow's Environment`() {
        MDC.put(key, "myValue") // 👍 : Correct behavior

        println("in test, $key: ${MDC.get(key)}") // 👍 : Correct behavior

        Environment(MDCContext()).unsafeRunSync {
            println("I am in ${getThreadName()}") // 👍 : Correct behavior

            println("in runBlocking, $key: ${MDC.get(key)}") // 👍 : Correct behavior

            parTupledN(
                ComputationPool + MDCContext(),
                suspend {
                    println("I am in parTupledN, ${getThreadName()}") // 👍 : Correct behavior

                    println("in parTupledN block, $key: ${MDC.get(key)}") // 👎 : expected MDC value
                },
                suspend {
                    println("I am in parTupledN, ${getThreadName()}") // 👍 : Correct behavior

                    println("in parTupledN block, $key: ${MDC.get(key)}") // 👎 : expected MDC value
                }
            )

            val result: Either<Throwable, String> = either {
                println("I am in ${getThreadName()}") // 👍 : Correct behavior

                println("in either block, $key: ${MDC.get(key)}") // 👎 : expected MDC value

                val threadName: String = evalOn(ComputationPool + MDCContext()) {
                    println("I am in ${getThreadName()}") // 👍 : Correct behavior

                    println("in evalOn block, $key: ${MDC.get(key)}") // 👎 : expected MDC value

                    getThreadName().right()
                }.bind()

                println("Evaluated thread result : $threadName in ${getThreadName()}") // 👎 : it should print `in thread name` on line #47

                println("in either block after evalOn, $key: ${MDC.get(key)}") // 👎 : expected MDC value

                parTupledN(
                    ComputationPool + MDCContext(),
                    suspend {
                        println("I am in ${getThreadName()}") // 👍 : Correct behavior

                        println("in parTupledN block after evalOn, $key: ${MDC.get(key)}") // 👎 : expected MDC value
                    },
                    suspend {
                        println("I am in ${getThreadName()}") // 👍 : Correct behavior

                        println("in parTupledN block after evalOn, $key: ${MDC.get(key)}") // 👎 : expected MDC value
                    }
                )

                threadName
            }

            println("Evaluated either block result : ${result.orNull()} in ${getThreadName()}") // 👎 : it should print `in thread name` on line #28

            println("in mdcContext after either block, $key: ${MDC.get(key)}") // 👎 : expected MDC value
        }

        println("in test, $key: ${MDC.get(key)}") // 👍 : Correct behavior
    }
}
