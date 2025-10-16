plugins {
  id("root.publication")
  kotlin("multiplatform") version libs.versions.kotlin apply false
  kotlin("jvm") version libs.versions.kotlin apply false
  id("com.github.gmazzo.buildconfig") version libs.versions.buildconfig
  id("com.gradle.plugin-publish") version libs.versions.gradle.plugin.publish
  id("org.jetbrains.kotlinx.binary-compatibility-validator") version libs.versions.binary.compatibility.validator apply false
}