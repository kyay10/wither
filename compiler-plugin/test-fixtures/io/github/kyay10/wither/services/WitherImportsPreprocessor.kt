package io.github.kyay10.wither.services

import org.jetbrains.kotlin.test.services.TestServices

class WitherImportsPreprocessor(testServices: TestServices, val defaultWither: String) :
  ImportsPreprocessor(testServices) {
  override val additionalImports: Set<String>
    get() = setOf("io.github.kyay10.wither.$defaultWither as defaultWither")
}
