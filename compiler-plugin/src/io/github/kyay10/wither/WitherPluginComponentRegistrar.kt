package io.github.kyay10.wither

import io.github.kyay10.wither.ir.WitherIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

class WitherPluginComponentRegistrar : CompilerPluginRegistrar() {
  override val pluginId: String
    get() = BuildConfig.KOTLIN_PLUGIN_ID
  override val supportsK2: Boolean
    get() = true

  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    FirExtensionRegistrarAdapter.registerExtension(WitherPluginRegistrar())
    IrGenerationExtension.registerExtension(WitherIrGenerationExtension())
  }
}
