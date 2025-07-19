plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.spring.dependency-management") version "1.1.4"
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenLocal()
    mavenCentral()
}

apply(plugin = "io.spring.dependency-management")
dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
    }
}

dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core:3.24.2")

    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "ru.usermng.ApplicationV2")
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}