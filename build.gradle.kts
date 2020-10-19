import io.kuberig.dsl.vanilla.plugin.KubeRigDslVanillaPluginExtension
import io.kuberig.dsl.vanilla.plugin.SemVersion

buildscript {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/teyckmans/rigeldev-oss-maven")
    }
    dependencies {
        classpath("io.kuberig:kuberig-dsl-generator-gradle-plugin:${version}")
        classpath("io.kuberig.dsl.vanilla.plugin:kuberig-dsl-vanilla-plugin:0.2.0")
    }
}

apply(plugin = "io.kuberig.dsl.vanilla.plugin")

configure<KubeRigDslVanillaPluginExtension> {
    gitHubOwner = "kubernetes"
    gitHubRepo = "kubernetes"
    startVersion = SemVersion(1, 14, 10)
    swaggerLocation = "api/openapi-spec/swagger.json"
}
