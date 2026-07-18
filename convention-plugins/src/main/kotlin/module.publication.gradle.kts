plugins {
  id("com.vanniktech.maven.publish")
}

mavenPublishing {
  pom {
    name.set("Wither")
    description.set(
      "A Kotlin Compiler Plugin that allows local `with` and `context` calls, so that adding receivers doesn't result in deeply nested code. This is especially important with context parameters."
    )
    url.set("https://github.com/kyay10/wither")

    licenses {
      license {
        name.set("Apache-2.0")
        url.set("https://opensource.org/license/apache-2-0")
      }
    }
    developers {
      developer {
        id.set("kyay10")
        name.set("Youssef Shoaib")
      }
    }
    scm {
      url.set("https://github.com/kyay10/wither")
    }
  }
}
