plugins {
    application
    java
}

group = "dev.jdsoft"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("dev.jdsoft.cronparser.Main")
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    testCompileOnly("org.projectlombok:lombok:1.18.38")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }

    // Include dependencies inside the jar
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}