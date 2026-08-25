plugins {
    java
    id("com.gradleup.shadow") version "9.0.0"
}

group = "com.slayerplayz.bmc5crossplay"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.117-stable")
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.shadowJar {
    archiveFileName.set("BMC5Crossplay-${project.version}.jar")
    relocate("org.sqlite", "com.slayerplayz.bmc5crossplay.libs.sqlite")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}