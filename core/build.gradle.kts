plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.jenetics:jenetics:7.1.3")

    // https://mvnrepository.com/artifact/io.jenetics/jenetics.ext
    implementation("io.jenetics:jenetics.ext:7.1.3")

    // https://mvnrepository.com/artifact/org.apache.commons/commons-math3
    implementation("org.apache.commons:commons-math3:3.6.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    //jvmToolchain(8)
}