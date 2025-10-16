plugins {
  `maven-publish`
  signing
}

publishing {
  // Configure all publications
  publications.withType<MavenPublication> {
    // Provide artifacts information required by Maven Central
    pom {
      name.set("Wither")
      description.set("A Kotlin Compiler Plugin that allows local `with` calls, so that adding receivers doesn't result in deeply nested code. This is especially important with context parameters.")
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
}

signing {
  if (project.hasProperty("signing.gnupg.keyName")) {
    useGpgCmd()
    sign(publishing.publications)
  }
}
