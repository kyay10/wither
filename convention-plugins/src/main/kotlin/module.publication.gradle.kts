plugins {
  `maven-publish`
  signing
}

publishing {
  // Configure all publications
  publications.withType<MavenPublication> {
    // Stub javadoc.jar artifact
    artifact(tasks.register("${name}JavadocJar", Jar::class) {
      archiveClassifier.set("javadoc")
      archiveAppendix.set(this@withType.name)
    })

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
