@file:Suppress("UnstableApiUsage")

pluginManagement {
  includeBuild("convention-plugins")
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://packages.jetbrains.team/maven/p/compiler-plugin-dev-kit/eap")
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
  id("com.gradleup.nmcp.settings") version "1.6.1"
  kotlin("compiler.plugin.devkit") version "0.0.3-dev-66e2b55"
}

nmcpSettings {
  centralPortal {
    val sonatypeUsername: String? by settings
    val sonatypePassword: String? by settings
    sonatypeUsername?.let { username = it }
    sonatypePassword?.let { password = sonatypePassword }
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/compiler-plugin-dev-kit/eap")
  }
}

rootProject.name = "wither"

include("compiler-plugin")

include("gradle-plugin")

include("plugin-annotations")
