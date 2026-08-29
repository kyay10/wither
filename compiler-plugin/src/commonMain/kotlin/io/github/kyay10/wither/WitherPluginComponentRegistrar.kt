package io.github.kyay10.wither

import io.github.kyay10.wither.ir.WitherIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar.ExtensionStorage
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@Suppress("unused") // Used via reflection.
class WitherPluginComponentRegistrar : DevKitComponentRegistrar {
  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    FirExtensionRegistrarAdapter.registerExtension(WitherPluginRegistrar())
    IrGenerationExtension.registerExtension(WitherIrGenerationExtension())
  }
}
