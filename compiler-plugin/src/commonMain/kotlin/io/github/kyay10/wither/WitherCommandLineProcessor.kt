package io.github.kyay10.wither

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitCLP
import org.jetbrains.kotlin.config.CompilerConfiguration

@Suppress("unused") // Used via reflection.
class WitherCommandLineProcessor : DevKitCLP {
  override val pluginOptions: Collection<CliOption> = emptyList()

  override fun processOption(
    option: AbstractCliOption,
    value: String,
    configuration: CompilerConfiguration,
  ) {
    error("Unexpected config option: '${option.optionName}'")
  }
}
