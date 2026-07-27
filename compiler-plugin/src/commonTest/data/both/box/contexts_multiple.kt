import io.github.kyay10.wither.contexts

fun box(): String {
  contexts("OK".let { it }, 42, Unit) {
    return contextOf<String>()
  }
}