import org.veupathdb.lib.gradle.container.util.Logger.Level

plugins {
  java
  id("org.veupathdb.lib.gradle.container.container-utils") version "3.1.0"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

val myProjectPackage = "org.veupathdb.service.demo"
val myMainClassName = "Main"

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
    projectPackage = myProjectPackage

    // Main Class Name
    mainClassName = myMainClassName
  }

  // Docker build configuration.
  docker {

    // Docker build context
    context = "."

    // Name of the target docker file
    dockerFile = "Dockerfile"
  }

  generateJaxRS {
    // List of custom arguments to use in the jax-rs code generation command
    // execution.
    arguments = listOf(/*arg1, arg2, arg3*/)

    // Map of custom environment variables to set for the jax-rs code generation
    // command execution.
    environment = mapOf(/*Pair("env-key", "env-val"), Pair("env-key", "env-val")*/)
  }

  tasks.shadowJar {
    manifest { attributes["Main-Class"] = "${myProjectPackage}.${myMainClassName}" }
    exclude("**/Log4j2Plugins.dat")
    archiveFileName.set("service.jar")
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

repositories {
  mavenCentral()
  maven {
    name = "GitHubPackages"
    url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
    credentials {
      username = if (extra.has("gpr.user")) extra["gpr.user"] as String? else System.getenv("GITHUB_USERNAME")
      password = if (extra.has("gpr.key")) extra["gpr.key"] as String? else System.getenv("GITHUB_TOKEN")
    }
  }
}

dependencies {

  //
  // Project Dependencies
  //

  // Core lib, prefers local checkout if available
  implementation(findProject(":core") ?: "org.veupathdb.lib:jaxrs-container-core:6.5.0")

  // Oracle
  runtimeOnly("com.oracle.database.jdbc:ojdbc8:12.2.0.1")

  // Jersey
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.0.4")
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-servlet:3.0.4")
  implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.0.4")
  runtimeOnly("org.glassfish.jersey.inject:jersey-hk2:3.0.4")

  // Jackson
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3")

  // Log4J
  implementation("org.apache.logging.log4j:log4j-api:2.17.2")
  implementation("org.apache.logging.log4j:log4j-core:2.17.2")

  // Metrics
  implementation("io.prometheus:simpleclient:0.15.0")
  implementation("io.prometheus:simpleclient_common:0.15.0")

  // Utils
  implementation("io.vulpine.lib:Jackfish:1.1.0")
  implementation("com.devskiller.friendly-id:friendly-id:1.1.0")

  // Unit Testing
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  testImplementation("org.mockito:mockito-core:4.3.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}
