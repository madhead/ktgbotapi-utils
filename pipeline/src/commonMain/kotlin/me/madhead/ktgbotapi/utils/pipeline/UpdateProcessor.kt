package me.madhead.ktgbotapi.utils.pipeline

import dev.inmo.tgbotapi.types.update.abstracts.Update

/**
 * Telegram [updates][Update] (messages, callbacks, etc.) processor.
 */
interface UpdateProcessor {
    /**
     * Returns a reaction to this [update] or `null` if this processor doesn't want to process this [update].
     */
    suspend fun process(update: Update): UpdateReaction?
}
