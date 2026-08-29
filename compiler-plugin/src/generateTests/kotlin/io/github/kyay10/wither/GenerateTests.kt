package io.github.kyay10.wither

import io.github.kyay10.wither.runners.AbstractContextJvmBoxTest
import io.github.kyay10.wither.runners.AbstractContextJvmDiagnosticTest
import io.github.kyay10.wither.runners.AbstractWithJvmBoxTest
import io.github.kyay10.wither.runners.AbstractWithJvmDiagnosticTest
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitTestGenerator
import org.jetbrains.kotlin.compiler.plugin.devkit.sourceSetTestClass

fun main(args: Array<String>) =
  DevKitTestGenerator.generate(args) {
    sourceSetTestClass<AbstractWithJvmDiagnosticTest> {
      model("both/diagnostics")
    }
    sourceSetTestClass<AbstractContextJvmDiagnosticTest> {
      model("both/diagnostics")
    }

    sourceSetTestClass<AbstractWithJvmBoxTest> {
      model("both/box")
    }
    sourceSetTestClass<AbstractContextJvmBoxTest> {
      model("both/box")
    }
  }
