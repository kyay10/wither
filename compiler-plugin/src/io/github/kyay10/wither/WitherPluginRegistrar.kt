package io.github.kyay10.wither

import io.github.kyay10.wither.fir.WitherReceiverGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class WitherPluginRegistrar : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::WitherReceiverGenerator
  }
}
