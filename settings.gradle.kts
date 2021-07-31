import java.util.Properties
import java.io.FileInputStream

val buildProps = Properties()
buildProps.load(FileInputStream(File(rootDir, "service.properties")))

rootProject.name = buildProps.getProperty("project.name")
  ?: error("failed to retrieve project name")

val core = file("../lib-jaxrs-container-core");
if (core.exists()) {
  include(":core")
  project(":core").projectDir = core
}

pluginManagement {
  repositories {
    maven {
      name = "GitHubPackages"
      url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
      credentials {
        username = extra["gpr.user"] as String? ?: System.getenv("GITHUB_USERNAME")
        password = extra["gpr.key"] as String? ?: System.getenv("GITHUB_TOKEN")
      }
    }
  }
}
