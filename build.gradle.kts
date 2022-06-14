import org.veupathdb.lib.gradle.container.util.Logger.Level

plugins {
  java
  id("org.veupathdb.lib.gradle.container.container-utils") version "3.4.0"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

containerBuild {

  // Change if debugging the build process is necessary.
  logLevel = Level.Trace

  // General project level configuration.
  project {

    // Project Name
    name = "demo-service"

    // Project Group
    group = "org.veupathdb.service"

    // Project Version
    version = "1.0.0"

    // Project Root Package
    projectPackage = "org.veupathdb.service.demo"

    // Main Class Name
    mainClassName = "Main"
  }

  // Docker build configuration.
  docker {

    // Docker build context
    context = "."

    // Name of the target docker file
    dockerFile = "Dockerfile"

    // Resulting image tag
    imageName = "example-service"

  }

  generateJaxRS {
    // List of custom arguments to use in the jax-rs code generation command
    // execution.
    arguments = listOf(/*arg1, arg2, arg3*/)

    // Map of custom environment variables to set for the jax-rs code generation
    // command execution.
    environment = mapOf(/*Pair("env-key", "env-val"), Pair("env-key", "env-val")*/)
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks.shadowJar {
  exclude("**/Log4j2Plugins.dat")
  archiveFileName.set("service.jar")
}

repositories {
  mavenCentral()
  mavenLocal()
  maven {
    name = "GitHubPackages"
    url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
    credentials {
      username = if (extra.has("gpr.user")) extra["gpr.user"] as String? else System.getenv("GITHUB_USERNAME")
      password = if (extra.has("gpr.key")) extra["gpr.key"] as String? else System.getenv("GITHUB_TOKEN")
    }
  }
}

configurations.all {
  resolutionStrategy {
    cacheChangingModulesFor(0, TimeUnit.SECONDS)
    cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
  }
}

dependencies {

  // Core lib
  implementation("org.veupathdb.lib:jaxrs-container-core:6.5.1")
  implementation("info.picocli:picocli:4.6.3")

  // Jersey
  implementation("org.glassfish.jersey.core:jersey-server:3.0.4")
  implementation("org.glassfish.jersey.media:jersey-media-multipart:3.0.4")

  // Jackson
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.3")

  // Logging
  implementation("org.slf4j:slf4j-api:1.7.36")
  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")

  // Internal libraries
  implementation("org.veupathdb.lib:hash-id:1.0.2")
  implementation("org.veupathdb.lib:jackson-singleton:3.0.0")

  // Async platform core
  implementation("org.veupathdb.lib:compute-platform:1.0-SNAPSHOT") { isChanging = true }

  // Metrics (can remove if not adding custom service metrics over those provided by container core)
  implementation("io.prometheus:simpleclient:0.15.0")
  implementation("io.prometheus:simpleclient_common:0.15.0")

  // Unit Testing
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  testImplementation("org.mockito:mockito-core:4.5.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}
