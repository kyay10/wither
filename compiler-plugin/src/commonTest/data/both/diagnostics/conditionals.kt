// RUN_PIPELINE_TILL: FRONTEND

fun box(): String {
  if (false) {
    defaultWither("OK", 42)
  }
  return <!NO_CONTEXT_ARGUMENT!>contextOf<!><String>()
}

/* GENERATED_FIR_TAGS: functionDeclaration, ifExpression, integerLiteral, stringLiteral */
