plugins {
    kotlin("jvm") version "1.8.0"
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    //jvmToolchain(8)
}