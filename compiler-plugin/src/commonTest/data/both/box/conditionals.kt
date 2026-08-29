fun box(): String {
  if (true) {
    defaultWither("OK", 42)
    return contextOf<String>()
  }
  else error("inaccessible")
}