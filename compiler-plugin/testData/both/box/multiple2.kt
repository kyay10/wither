fun box(): String {
  defaultWither("Fail", 42)
  defaultWither(42, "OK")
  return contextOf<String>()
}