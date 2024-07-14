package me.madhead.ktgbotapi.utils.pipeline

import dev.inmo.tgbotapi.types.update.abstracts.Update

/**
 * A reaction to an [update][Update].
 */
typealias UpdateReaction = suspend () -> Unit
