plugins {
    java
    `java-gradle-plugin`
}

repositories {
    jcenter()
}

dependencies {
    compileOnly(gradleApi())

    implementation("com.mashape.unirest:unirest-java:1.4.9")

    implementation("com.fasterxml.jackson.core:jackson-core:2.9.8")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation("com.fasterxml.jackson.module:jackson-modules-java8:2.9.8")
}


gradlePlugin {
    plugins {
        create("dsl-projects-generator-plugin") {
            id = "dsl-projects-generator-plugin"
            implementationClass = "eu.rigeldev.kuberig.dsl.kubernetes.DslProjectsGeneratorPlugin"
        }
    }
}

