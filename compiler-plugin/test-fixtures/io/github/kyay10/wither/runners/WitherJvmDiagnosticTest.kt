package io.github.kyay10.wither.runners

import io.github.kyay10.wither.services.ExtensionRegistrarConfigurator
import io.github.kyay10.wither.services.PluginAnnotationsProvider
import io.github.kyay10.wither.services.WitherImportsPreprocessor
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives.DISABLE_FIR_DUMP_HANDLER
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives
import org.jetbrains.kotlin.test.runners.AbstractFirPhasedDiagnosticTest
import org.jetbrains.kotlin.test.services.EnvironmentBasedStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider
import org.jetbrains.kotlin.utils.bind

open class WitherJvmDiagnosticTest : AbstractFirPhasedDiagnosticTest(FirParser.LightTree) {
  override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider {
    return EnvironmentBasedStandardLibrariesPathProvider
  }

  override fun configure(builder: TestConfigurationBuilder) {
    super.configure(builder)

    with(builder) {
      /*
       * Containers of different directives, which can be used in tests:
       * - ModuleStructureDirectives
       * - LanguageSettingsDirectives
       * - DiagnosticsDirectives
       * - FirDiagnosticsDirectives
       *
       * All of them are located in `org.jetbrains.kotlin.test.directives` package
       */
      defaultDirectives {
        +FirDiagnosticsDirectives.FIR_DUMP
        +JvmEnvironmentConfigurationDirectives.FULL_JDK

        +CodegenTestDirectives.IGNORE_DEXING // Avoids loading R8 from the classpath.
      }

      useConfigurators(
        ::PluginAnnotationsProvider,
        ::ExtensionRegistrarConfigurator
      )
    }
  }
}

open class AbstractWithJvmDiagnosticTest : WitherJvmDiagnosticTest() {
  override fun configure(builder: TestConfigurationBuilder) {
    super.configure(builder)
    builder.useSourcePreprocessor(::WitherImportsPreprocessor.bind("with"))
  }
}

open class AbstractContextJvmDiagnosticTest : WitherJvmDiagnosticTest() {
  override fun configure(builder: TestConfigurationBuilder) {
    super.configure(builder)
    builder.useSourcePreprocessor(::WitherImportsPreprocessor.bind("context"))
    builder.defaultDirectives { +DISABLE_FIR_DUMP_HANDLER }
  }
}