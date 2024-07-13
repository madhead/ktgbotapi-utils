package me.madhead.ktgbotapi.utils.pipeline

import dev.inmo.kslog.common.KSLog
import dev.inmo.kslog.common.debug
import dev.inmo.kslog.common.logger
import dev.inmo.kslog.common.warning
import dev.inmo.tgbotapi.types.update.abstracts.Update

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
        private val logger = KSLog.logger
    }

    /**
     * Process the [update][Update].
     */
    suspend fun process(update: Update) {
        logger.debug { "Processing update: $update" }

        processors
            .mapNotNull { it.process(update) }
            .also { reactions ->
                logger.debug { "Reactions (${reactions.size}): ${reactions.map { it::class }}" }
                if (reactions.size != 1) {
                    logger.warning { "No suitable processor found or found more than one" }
                }
            }
            .singleOrNull()
            ?.invoke()
    }
}
