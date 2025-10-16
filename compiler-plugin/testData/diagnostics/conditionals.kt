// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +ContextParameters

import io.github.kyay10.wither.*

fun box(): String {
  if (false) {
    with("OK", 42)
  }
  return <!NO_CONTEXT_ARGUMENT!>contextOf<!><String>()
}

/* GENERATED_FIR_TAGS: functionDeclaration, ifExpression, integerLiteral, stringLiteral */
