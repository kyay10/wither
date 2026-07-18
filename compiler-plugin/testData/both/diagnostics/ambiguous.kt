// RUN_PIPELINE_TILL: FRONTEND

fun box(): String {
  defaultWither("Fail2", 42, "OK")
  return <!AMBIGUOUS_CONTEXT_ARGUMENT!>contextOf<!><String>()
}

/* GENERATED_FIR_TAGS: functionDeclaration, ifExpression, integerLiteral, stringLiteral */
