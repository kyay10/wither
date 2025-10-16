// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +ContextParameters

import io.github.kyay10.wither.*

fun box(): String {
  with("Fail2", 42, "OK")
  return <!AMBIGUOUS_CONTEXT_ARGUMENT!>contextOf<!><String>()
}

/* GENERATED_FIR_TAGS: functionDeclaration, ifExpression, integerLiteral, stringLiteral */
