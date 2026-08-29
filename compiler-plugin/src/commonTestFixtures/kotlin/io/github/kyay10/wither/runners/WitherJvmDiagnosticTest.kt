package io.github.kyay10.wither.runners

import io.github.kyay10.wither.WitherPluginComponentRegistrar
import io.github.kyay10.wither.services.WitherImportsPreprocessor
import org.jetbrains.kotlin.compiler.plugin.devkit.runners.DevKitJvmDiagnosticTest
import org.jetbrains.kotlin.compiler.plugin.devkit.services.configurePlugin
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives.DISABLE_FIR_DUMP_HANDLER
import org.jetbrains.kotlin.utils.bind

open class WitherJvmDiagnosticTest(vararg config: TestConfigurationBuilder.() -> Unit) :
  DevKitJvmDiagnosticTest({ configurePlugin(WitherPluginComponentRegistrar()) }, *config)

open class AbstractWithJvmDiagnosticTest :
  WitherJvmDiagnosticTest({ useSourcePreprocessor(::WitherImportsPreprocessor.bind("with")) })

open class AbstractContextJvmDiagnosticTest :
  WitherJvmDiagnosticTest({
    useSourcePreprocessor(::WitherImportsPreprocessor.bind("context"))
    defaultDirectives { +DISABLE_FIR_DUMP_HANDLER }
  })
