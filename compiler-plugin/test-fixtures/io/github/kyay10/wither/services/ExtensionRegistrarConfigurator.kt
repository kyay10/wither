package io.github.kyay10.wither.services

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import io.github.kyay10.wither.WitherPluginRegistrar
import io.github.kyay10.wither.ir.WitherIrGenerationExtension
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices

class ExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
  override fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerExtensions(
    module: TestModule,
    configuration: CompilerConfiguration
  ) {
    FirExtensionRegistrarAdapter.registerExtension(WitherPluginRegistrar())
    IrGenerationExtension.registerExtension(WitherIrGenerationExtension())
  }
}
