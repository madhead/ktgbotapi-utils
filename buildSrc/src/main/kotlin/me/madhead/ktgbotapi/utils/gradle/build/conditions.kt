package me.madhead.ktgbotapi.utils.gradle.build

import java.lang.System.getenv as env

val CI: Boolean
    get() = env("CI") != null

val GITHUB_ACTIONS: Boolean
    get() = env("GITHUB_ACTIONS") != null
