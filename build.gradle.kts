plugins {
    `java-library`
    application
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
    implementation("com.fifesoft:rsyntaxtextarea:4.0.1")
    implementation("com.fifesoft:spellchecker:3.4.1")
    implementation("com.fifesoft:rstaui:4.0.0")
    implementation("com.fifesoft:autocomplete:4.0.0")
    implementation("com.formdev:flatlaf:3.7.2")
    implementation("com.formdev:flatlaf-intellij-themes:3.7.2")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.jackson)
    implementation(libs.jacksondatabind)
    implementation(libs.jspecify)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(27)
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