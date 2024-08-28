import me.madhead.ktgbotapi.utils.gradle.build.GITHUB_ACTIONS
import pl.allegro.tech.build.axion.release.domain.PredefinedVersionCreator
import pl.allegro.tech.build.axion.release.domain.PredefinedVersionCreator.VERSION_WITH_BRANCH
import java.lang.System.getenv as env

plugins {
    id("pl.allegro.tech.build.axion-release")
}

scmVersion {
    versionCreator("versionWithBranch")
    snapshotCreator { _, _ -> "" }
}

version = when {
    GITHUB_ACTIONS -> {
        val sha = env("GITHUB_SHA")
        val runId = env("GITHUB_RUN_ID")

        "${scmVersion.version}+${sha.take(7)}.$runId"
    }

    else -> scmVersion.version
}
