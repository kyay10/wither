package io.github.kyay10.wither

import io.github.kyay10.wither.fir.WitherContextArgumentCleaner
import io.github.kyay10.wither.fir.WitherImplicitValueGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class WitherPluginRegistrar : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::WitherImplicitValueGenerator
    +::WitherContextArgumentCleaner
  }
}
