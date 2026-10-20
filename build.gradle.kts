plugins {
    `java-library`
    application
    alias(libs.plugins.shadow)
    alias(libs.plugins.javafx)
}

application {
    mainClass = "Main"
    applicationDefaultJvmArgs = listOf(
        "-Xmx1g",
        "-Dawt.useSystemAAFontSettings=on",
        "-Dswing.aatext=true",
    )
}

group = "com.imjustdoom.doomsjde"
version = "1.0-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(27))
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.fxmisc.richtext:richtextfx:0.11.7")

    implementation(libs.bundles.fifesoft)
    implementation(libs.bundles.formdev)
    implementation(libs.bundles.jackson)

    implementation(libs.atlantafx.base)
    implementation(libs.jspecify)
}

javafx {
    version = "27"
    modules(
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics"
    )
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(27)
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
    }

    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }

    test {
        useJUnitPlatform()
    }
}