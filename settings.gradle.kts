@file:Suppress("UnstableApiUsage")

pluginManagement {
  includeBuild("convention-plugins")
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
  id("com.gradleup.nmcp.settings").version("1.6.1")
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
  }
}

rootProject.name = "wither"

include("compiler-plugin")
include("gradle-plugin")
include("plugin-annotations")
