package io.github.kyay10.wither

import io.github.kyay10.wither.fir.WitherCallInjector
import io.github.kyay10.wither.fir.WitherContextArgumentCleaner
import io.github.kyay10.wither.fir.WitherImplicitValueGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionApiInternals
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class WitherPluginRegistrar : FirExtensionRegistrar() {
  @OptIn(FirExtensionApiInternals::class)
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::WitherImplicitValueGenerator
    +::WitherCallInjector
    +::WitherContextArgumentCleaner
  }
}
