import io.github.kyay10.wither.contexts

fun box(): String {
  contexts(
    "OK",
    block = {
      return contextOf<String>()
    },
  )
}
