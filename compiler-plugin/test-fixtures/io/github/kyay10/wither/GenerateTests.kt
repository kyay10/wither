package io.github.kyay10.wither

import io.github.kyay10.wither.runners.AbstractContextJvmBoxTest
import io.github.kyay10.wither.runners.AbstractContextJvmDiagnosticTest
import io.github.kyay10.wither.runners.AbstractWithJvmBoxTest
import io.github.kyay10.wither.runners.AbstractWithJvmDiagnosticTest
import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5

fun main() {
  generateTestGroupSuiteWithJUnit5 {
    testGroup(testDataRoot = "compiler-plugin/testData", testsRoot = "compiler-plugin/test-gen") {
      testClass<AbstractWithJvmDiagnosticTest> {
        model("both/diagnostics")
      }
      testClass<AbstractContextJvmDiagnosticTest> {
        model("both/diagnostics")
      }

      testClass<AbstractWithJvmBoxTest> {
        model("both/box")
      }
      testClass<AbstractContextJvmBoxTest> {
        model("both/box")
      }
    }
  }
}
