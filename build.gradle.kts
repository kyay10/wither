plugins {
  id("root.publication")
  kotlin("multiplatform") version libs.versions.kotlin apply false
  kotlin("jvm") version libs.versions.kotlin apply false
  id("com.github.gmazzo.buildconfig") version libs.versions.buildconfig
  id("com.gradle.plugin-publish") version libs.versions.gradle.plugin.publish apply false
  alias(libs.plugins.spotless)
}

spotless {
  val excludes =
    arrayOf(
      "**/build/**",
      "**/.gradle/**",
      "**/.kotlin/**",
      "**/testData/**",
      "**/data/**",
    )
  kotlin {
    target("**/*.kt")
    targetExclude(*excludes)
    ktfmt(libs.versions.ktfmt.get()).googleStyle()
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    targetExclude(*excludes)
    ktfmt(libs.versions.ktfmt.get()).googleStyle()
  }
}
