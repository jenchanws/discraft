plugins {
  kotlin("jvm") version "1.8.10"
  java
  id("fabric-loom") version "1.1-SNAPSHOT"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

val mod_version: String by project
val minecraft_version: String by project
val maven_group: String by project
val archives_base_name: String by project

version = "$mod_version+mc$minecraft_version"
group = maven_group

repositories {
  mavenCentral()
  jcenter()
  maven(url = "https://maven.shedaniel.me/")
  maven(url = "https://maven.terraformersmc.com/releases/")
  maven(url = "https://maven.nucleoid.xyz/")
  maven(url = "https://m2.dv8tion.net/releases")
  maven(url = "https://jitpack.io")
}

fun DependencyHandler.includeModImpl(dep: String) {
  this.modImplementation(dep)
  this.include(dep)
}

fun DependencyHandler.includeModImpl(dep: String, action: Action<ExternalModuleDependency>) {
  this.modImplementation(dep)
  this.include(dep, action)
}

fun DependencyHandler.includeImpl(dep: String) {
  this.implementation(dep)
  this.include(dep)
}

fun DependencyHandler.includeImpl(dep: String, action: Action<ExternalModuleDependency>) {
  this.implementation(dep)
  this.include(dep, action)
}

dependencies {
  val loader_version: String by project
  val fabric_version: String by project
  val fabric_kotlin_version: String by project

  minecraft("com.mojang:minecraft:$minecraft_version")
  mappings(loom.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:$loader_version")
  modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")
  modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")

  val placeholder_version: String by project
  val jda_version: String by project
  val webhook_version: String by project

  includeModImpl("eu.pb4:placeholder-api:$placeholder_version")

  includeImpl("net.dv8tion:JDA:$jda_version") {
    exclude(module = "opus-java")
  }
  includeImpl("club.minnced:discord-webhooks:$webhook_version")

  include("org.json:json:20160212")
  include("com.squareup.okio:okio:1.17.2")
  include("com.squareup.okhttp3:okhttp:3.13.0")
  include("org.apache.commons:commons-collections4:4.4")
  include("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
  include("com.neovisionaries:nv-websocket-client:2.14")
  include("net.sf.trove4j:trove4j:3.0.3")
  include("com.fasterxml.jackson.core:jackson-databind:2.13.1")
  include("com.fasterxml.jackson.core:jackson-annotations:2.13.1")
  include("com.fasterxml.jackson.core:jackson-core:2.13.1")
}

val targetJavaVersion = 17

tasks {
  processResources {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
      expand("version" to project.version)
    }
  }

  compileKotlin {
    kotlinOptions.jvmTarget = targetJavaVersion.toString()
  }

  withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
      options.release.set(targetJavaVersion)
    }
  }

  jar {
    from("LICENSE") {
      rename { "${it}_${archives_base_name}" }
    }

    archiveBaseName.set(archives_base_name)
  }
}

java {
  val javaVersion = JavaVersion.toVersion(targetJavaVersion)
  if (JavaVersion.current() < javaVersion) {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
  }
}
