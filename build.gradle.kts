plugins {
    kotlin("jvm") version("2.3.0")
    id("java")
    id("maven-publish")
}


group = "net.mcbrawls"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
}

dependencies {
    api("com.mojang:datafixerupper:7.0.14")
    api("org.apache.commons:commons-lang3:3.20.0")
}

kotlin {
    jvmToolchain(25)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String
        }
    }

    repositories {
        val mavenUrl = System.getenv("MAVEN_URL")
        if (mavenUrl != null) {
            maven {
                name = "envmaven"
                url = uri(mavenUrl)
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }
}
