import org.veupathdb.lib.gradle.container.util.Logger.Level

plugins {
  java
  id("org.veupathdb.lib.gradle.container.container-utils") version "3.1.0"
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
  }

  // FgpUtil download configuration.
  fgputil {
    // Select the FgpUtil tag to use as a dependency.
    //
    // This may be omitted to use "latest", however that is discouraged as it
    // may cause build issues in the future if FgpUtil introduces any breaking
    // changes.
    //
    // For a full list of available tags, see: https://github.com/VEuPathDB/FgpUtil/tags
    version = "4f2eb70"

    // Optionally limit the dependencies to include from FgpUtil using this
    // array:
    // targets = arrayOf(
    //   AccountDB,
    //   Cache,
    //   CLI,
    //   Client,
    //   Core,
    //   DB,
    //   Events,
    //   JSON,
    //   Server,
    //   Servlet,
    //   Solr,
    //   Test,
    //   Web,
    //   XML,
    // )
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
  // FgpUtil & Compatibility Dependencies
  //

  // FgpUtil jars
  implementation(files(
    "vendor/fgputil-accountdb-1.0.0.jar",
    "vendor/fgputil-core-1.0.0.jar",
    "vendor/fgputil-db-1.0.0.jar",
    "vendor/fgputil-web-1.0.0.jar"
  ))

  // Compatibility bridge to support the long dead log4j-1.X
  runtimeOnly("org.apache.logging.log4j:log4j-1.2-api:2.17.0")

  // Extra FgpUtil dependencies
  runtimeOnly("org.apache.commons:commons-dbcp2:2.8.0")
  runtimeOnly("org.json:json:20211205")
  runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-json-org:2.13.1")
  runtimeOnly("com.fasterxml.jackson.module:jackson-module-parameter-names:2.13.1")
  runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.13.1")
  runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")

  //
  // Project Dependencies
  //

  // Oracle
  runtimeOnly(files(
    "vendor/ojdbc8.jar",
    "vendor/ucp.jar",
    "vendor/xstreams.jar"
  ))


  // Core lib, prefers local checkout if available
  implementation("org.veupathdb.lib:jaxrs-container-core:6.0.1")


  // Jersey
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.0.4")
  implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-servlet:3.0.4")
  implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.0.4")
  runtimeOnly("org.glassfish.jersey.inject:jersey-hk2:3.0.4")

  // Jackson
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1")

  // Log4J
  implementation("org.apache.logging.log4j:log4j-api:2.17.0")
  implementation("org.apache.logging.log4j:log4j-core:2.17.0")
  implementation("org.apache.logging.log4j:log4j:2.17.0")

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
