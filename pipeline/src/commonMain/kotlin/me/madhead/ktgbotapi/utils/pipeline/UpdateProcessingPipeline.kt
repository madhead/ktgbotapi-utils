package me.madhead.ktgbotapi.utils.pipeline

import dev.inmo.kslog.common.KSLog
import dev.inmo.kslog.common.debug
import dev.inmo.kslog.common.error
import dev.inmo.kslog.common.logger
import dev.inmo.kslog.common.warning
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

/**
 * A pipeline of [UpdateProcessor]s.
 *
 * Basically, it is the heart of all input processing of any bot: every update is passed to [process] and processed by
 * at most one of the [UpdateProcessor]s, whose [UpdateProcessor.process] returned non-null [UpdateReaction].
 */
class UpdateProcessingPipeline(
    private val processors: List<UpdateProcessor>,
) {
    companion object {
        private val log = KSLog.logger
    }

    /**
     * Process the [update][Update].
     */
    suspend fun process(update: Update) = supervisorScope {
        log.debug { "Processing update: $update" }

        val reactions = processors
            .map {
                async(Dispatchers.Default) {
                    try {
                        it.process(update)
                    } catch (e: Exception) {
                        logger.error(e) { "Error processing update: $update" }

                        null
                    }
                }
            }
            .awaitAll()
            .filterNotNull()

        when (reactions.size) {
            0 -> logger.warning { "No suitable processor found" }
            1 -> reactions.single().invoke()
            else -> throw IllegalStateException("Found more than one suitable processor")
        }
    }
}
