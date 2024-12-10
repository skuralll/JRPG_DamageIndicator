plugins {
    kotlin("jvm") version "1.9.22"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

group = "com.skuralll"
version = "0.2.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

tasks {

    javadoc {
        options.encoding = "UTF-8"
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
        }
    }

    create<Jar>("sourceJar") {
        archiveClassifier.set("source")
        from(sourceSets["main"].allSource)
    }
}

tasks.register<Copy>("deployPlugin") {
    dependsOn("build")
    val jarFile = tasks.named<Jar>("jar").get().archiveFile.get().asFile
    from(file(jarFile))
    into(env.DEST_DIR.value)
    doLast {
        exec {
            commandLine(
                "mcrcon",
                "-H",
                env.HOST_IP.value,
                "-P",
                env.HOST_PORT.value,
                "-p",
                env.PASSWORD.value,
                "plugman reload ${rootProject.name}"
            )
        }
    }
}