import java.net.URI

plugins {
    kotlin("jvm")
    id("maven-publish")
}

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.jenetics:jenetics:7.1.3")

    // https://mvnrepository.com/artifact/io.jenetics/jenetics.ext
    implementation("io.jenetics:jenetics.ext:7.1.3")

    // https://mvnrepository.com/artifact/org.apache.commons/commons-math3
    implementation("org.apache.commons:commons-math3:3.6.1")

    // https://mvnrepository.com/artifact/com.google.ortools/ortools-java
    implementation("com.google.ortools:ortools-java:9.6.2534")


    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    //jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "it.github.petretiandrea.mealplanner"
            artifactId = "meal-planner-core"
            version = "1.0.0"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/octocat/hello-world")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}