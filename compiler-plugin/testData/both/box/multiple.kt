fun box(): String {
  defaultWither("OK", 42)
  return contextOf<String>()
}