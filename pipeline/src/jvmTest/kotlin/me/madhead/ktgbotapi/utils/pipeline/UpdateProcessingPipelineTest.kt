package me.madhead.ktgbotapi.utils.pipeline

import dev.inmo.tgbotapi.types.UpdateId
import dev.inmo.tgbotapi.types.update.abstracts.UnknownUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds

internal class UpdateProcessingPipelineTest {
    @Test
    fun `process should not fail when the list of processors is empty`() = runTest {
        val sut = UpdateProcessingPipeline(
            processors = emptyList()
        )

        sut.process(update)
    }

    @Test
    fun `process should not fail when no processors match`() = runTest {
        val sut = UpdateProcessingPipeline(
            processors = listOf(
                UpdateProcessor { null },
                UpdateProcessor { null },
            )
        )

        sut.process(update)
    }

    @Test
    fun `process should fail when more than one processor match`() = runTest {
        val sut = UpdateProcessingPipeline(
            processors = listOf(
                UpdateProcessor { {} },
                UpdateProcessor { {} },
            )
        )

        assertThrows(IllegalStateException::class.java) {
            runBlocking {
                sut.process(update)
            }
        }
    }

    @Test
    fun `process should call the reaction`() = runTest {
        val completion = AtomicBoolean(false)
        val sut = UpdateProcessingPipeline(
            processors = listOf(
                UpdateProcessor { null },
                UpdateProcessor { null },
                UpdateProcessor { { completion.set(true) } },
                UpdateProcessor { null },
                UpdateProcessor { null },
            )
        )

        sut.process(update)

        assertTrue(completion.get())
    }

    @Test
    fun `process should call the reaction even when the other processor failed`() = runTest {
        val completion = AtomicBoolean(false)
        val sut = UpdateProcessingPipeline(
            processors = listOf(
                UpdateProcessor { throw Exception() },
                UpdateProcessor { { completion.set(true) } },
            )
        )

        sut.process(update)

        assertTrue(completion.get())
    }

    @Test
    fun `process should rethrow the exception from the reaction`() = runTest {
        val completion = AtomicBoolean(false)
        val sut = UpdateProcessingPipeline(
            processors = listOf(
                UpdateProcessor { null },
                UpdateProcessor {
                    {
                        completion.set(true)
                        throw IllegalStateException()
                    }
                },
            )
        )

        assertThrows(IllegalStateException::class.java) {
            runBlocking {
                sut.process(update)
            }
        }
        assertTrue(completion.get())
    }

    @Test
    fun `process should select the reaction in parallel`() = runTest {
        val completion = AtomicBoolean(false)
        val sut = UpdateProcessingPipeline(
            processors = List(5) { index ->
                UpdateProcessor {
                    delay((index + 1).seconds)
                    if (index == 2) {
                        suspend { completion.set(true) }
                    } else {
                        null
                    }
                }
            }
        )

        withContext(Dispatchers.Default.limitedParallelism(1)) {
            // Max delay in the list is 5 seconds, while total delay 15 seconds
            // So, we should be able to process the update in about 5 seconds
            // Adding 1 second as a safety margin
            withTimeout(6.seconds) {
                sut.process(update)
            }
        }

        assertTrue(completion.get())
    }

    companion object {
        private val update = UnknownUpdate(UpdateId(1), JsonNull)
    }
}
