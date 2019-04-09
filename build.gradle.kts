import com.jfrog.bintray.gradle.BintrayExtension

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
    }
}

plugins {
    id("dsl-projects-generator-plugin")
}

subprojects {
    apply {
        plugin("com.jfrog.bintray")
        plugin("maven-publish")
        plugin("java")
        plugin("idea")
    }

    val subProject = this

    subProject.group = "eu.rigeldev.kuberig.dsl.kubernetes"
    subProject.version = project.version

    repositories {
        jcenter()
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")

        val sourceSets: SourceSetContainer by subProject
        from(sourceSets["main"].allSource)
    }

    configure<PublishingExtension> {

        publications {
            register(subProject.name, MavenPublication::class) {
                from(components["java"])
                artifact(sourcesJar.get())
            }
        }

    }

    configure<BintrayExtension> {
        val bintrayApiKey : String by subProject
        val bintrayUser : String by subProject

        user = bintrayUser
        key = bintrayApiKey
        publish = true

        pkg(closureOf<BintrayExtension.PackageConfig>{
            repo = "rigeldev-oss-maven"
            name = subProject.name
            setLicenses("Apache-2.0")
            vcsUrl = "https://github.com/teyckmans/kuberig-dsl-kubernetes"
        })

        setPublications(subProject.name)
    }
}