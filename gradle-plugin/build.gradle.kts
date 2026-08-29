plugins {
  pluginDevKit("gradle-plugin")
  id("module.publication")
}

gradlePlugin {
  website = "https://github.com/kyay10/wither"
  vcsUrl = "https://github.com/kyay10/wither"
  plugins {
    create("WitherPlugin") {
      id = rootProject.group.toString()
      displayName = "Wither"
      description =
        "A Kotlin Compiler Plugin that allows local `with` and `context` calls, so that adding receivers doesn't result in deeply nested code. This is especially important with context parameters."
      tags = listOf("kotlin-compiler-plugin")
      implementationClass = "io.github.kyay10.wither.WitherGradlePlugin"
    }
  }
}
