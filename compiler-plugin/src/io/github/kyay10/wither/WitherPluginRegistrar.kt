package io.github.kyay10.wither

import io.github.kyay10.wither.fir.WitherClassGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class WitherPluginRegistrar : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::WitherClassGenerator
  }
}
