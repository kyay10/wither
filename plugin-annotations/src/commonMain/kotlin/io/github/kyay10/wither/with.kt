package io.github.kyay10.wither

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public fun with(@Suppress("unused") vararg values: Any?) {
  error("Should be replaced by the Wither plugin")
}

public fun context(@Suppress("unused") vararg values: Any?) {
  error("Should be replaced by the Wither plugin")
}

public class Contexts(private val values: Array<out Any?>) {
  @Suppress("UNCHECKED_CAST") internal operator fun <T> get(index: Int): T = values[index] as T
}

context(c: Contexts)
public fun <T> getContextHere(index: Int): T = c[index]

public val insertContextCallHere: Unit
  get() = Unit

@OptIn(ExperimentalContracts::class)
public inline fun contexts(vararg values: Any?, block: context(Contexts) () -> Unit) {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  block(Contexts(values))
}
