import org.veupathdb.lib.gradle.container.util.Logger.Level

plugins {
  java
  id("org.veupathdb.lib.gradle.container.container-utils") version "4.0.0"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

// configure VEupathDB container plugin
containerBuild {

  // Change if debugging the build process is necessary.
  logLevel = Level.Info

  // General project level configuration.
  project {

    // Project Name
    name = "sequence-retrieval"

    // Project Group
    group = "org.veupathdb.service"

    // Project Version
    version = "1.0.0"

    // Project Root Package
    projectPackage = "org.veupathdb.service.sr"

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
    imageName = "sequence-retrieval"

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

  // Working with sequences
  implementation("com.github.samtools:htsjdk:2.24.1")

  // FgpUtil
  implementation("org.gusdb:fgputil-core:2.7.1-jakarta")

  // Core lib
  implementation("org.veupathdb.lib:jaxrs-container-core:6.8.0")

  // Jersey
  implementation("org.glassfish.jersey.core:jersey-server:3.0.8")
  implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.0.8")
  implementation("org.glassfish.jersey.media:jersey-media-multipart:3.0.8")

  // Async platform core
  implementation("org.veupathdb.lib:compute-platform:1.3.4")

  // Job IDs
  implementation("org.veupathdb.lib:hash-id:1.1.0")

  // Logging
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.apache.logging.log4j:log4j-core:2.19.0")
  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")

  // MapDB
  implementation("org.mapdb:mapdb:3.0.8")

  // Pico CLI
  // Only required if your project adds custom CLI/environment options, see
  // the "MyOptions" class in the demo source code.
  implementation("info.picocli:picocli:4.6.3")

  // Multipart/Form-Data Jersey Plugin
  // Only required if your project will be handling multipart/form-data HTTP
  // requests.
  implementation("org.glassfish.jersey.media:jersey-media-multipart:3.0.4")

  // Jackson
  implementation("org.veupathdb.lib:jackson-singleton:3.0.0")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.4")

  // Metrics
  implementation("io.prometheus:simpleclient:0.16.0")
  implementation("io.prometheus:simpleclient_common:0.16.0")

  // Unit Testing
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testImplementation("org.mockito:mockito-core:4.8.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}
