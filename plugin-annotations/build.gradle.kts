@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
  kotlin("multiplatform")
  id("module.publication")
}

kotlin {
  explicitApi()
  @OptIn(ExperimentalAbiValidation::class) abiValidation()

  androidNativeArm32()
  androidNativeArm64()
  androidNativeX64()
  androidNativeX86()

  iosArm64()
  iosSimulatorArm64()
  iosX64()

  js().nodejs()

  jvmToolchain(8)
  jvm()

  linuxArm64()
  linuxX64()

  macosArm64()

  mingwX64()

  tvosArm64()
  tvosSimulatorArm64()

  wasmJs().nodejs()
  wasmWasi().nodejs()

  watchosArm32()
  watchosArm64()
  watchosDeviceArm64()
  watchosSimulatorArm64()

  applyDefaultHierarchyTemplate()
}
