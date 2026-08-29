package io.github.kyay10.wither.runners

import io.github.kyay10.wither.WitherPluginComponentRegistrar
import io.github.kyay10.wither.services.WitherImportsPreprocessor
import org.jetbrains.kotlin.compiler.plugin.devkit.TestDumpDirectives.DUMP_CLASSIFIER
import org.jetbrains.kotlin.compiler.plugin.devkit.runners.DevKitJvmBoxTest
import org.jetbrains.kotlin.compiler.plugin.devkit.services.configurePlugin
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.LanguageSettingsDirectives.LANGUAGE
import org.jetbrains.kotlin.utils.bind

open class WitherJvmBoxTest(vararg config: TestConfigurationBuilder.() -> Unit) :
  DevKitJvmBoxTest(
    {
      configurePlugin(WitherPluginComponentRegistrar())
      defaultDirectives { LANGUAGE with "+ContextParameters" }
    },
    *config,
  )

open class AbstractWithJvmBoxTest :
  WitherJvmBoxTest({ useSourcePreprocessor(::WitherImportsPreprocessor.bind("with")) })

open class AbstractContextJvmBoxTest :
  WitherJvmBoxTest({
    useSourcePreprocessor(::WitherImportsPreprocessor.bind("context"))
    defaultDirectives { DUMP_CLASSIFIER with ContextParametersClassifier.classifier }
  })
