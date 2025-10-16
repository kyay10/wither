package io.github.kyay10.wither

import io.github.kyay10.wither.runners.AbstractJvmBoxTest
import io.github.kyay10.wither.runners.AbstractJvmDiagnosticTest
import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5

fun main() {
  generateTestGroupSuiteWithJUnit5 {
    testGroup(testDataRoot = "compiler-plugin/testData", testsRoot = "compiler-plugin/test-gen") {
      testClass<AbstractJvmDiagnosticTest> {
        model("diagnostics")
      }

      testClass<AbstractJvmBoxTest> {
        model("box")
      }
    }
  }
}
