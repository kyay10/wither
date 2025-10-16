package io.github.kyay10.wither.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

class WitherIrGenerationExtension : IrGenerationExtension {
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    val transformers = listOf(WitherIrBodyGenerator(pluginContext))
    for (transformer in transformers) {
      moduleFragment.acceptChildrenVoid(transformer)
    }
  }
}
