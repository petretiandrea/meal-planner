plugins {
    kotlin("jvm") version "1.8.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.jenetics:jenetics:7.1.3")

    // https://mvnrepository.com/artifact/io.jenetics/jenetics.ext
    implementation("io.jenetics:jenetics.ext:7.1.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    //jvmToolchain(8)
}