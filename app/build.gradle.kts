plugins {
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
  application
}

repositories { mavenCentral() }

dependencies { implementation("commons-cli:commons-cli:1.4") }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }

tasks.withType<Jar> {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  manifest { attributes["Main-Class"] = "com.teixeira.gdx.AppKt" }

  from({
    configurations.runtimeClasspath
      .get()
      .filter { it.exists() }
      .map { if (it.isDirectory) it else zipTree(it) }
  })
}

tasks.build { dependsOn(tasks.jar) }
