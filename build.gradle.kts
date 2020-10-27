import io.kuberig.dsl.vanilla.plugin.SemVersion

plugins {
    id("io.kuberig.dsl.vanilla.plugin")
}

kubeRigDslVanilla {
    gitHubOwner = "kubernetes"
    gitHubRepo = "kubernetes"
    startVersion = SemVersion(1, 14, 10)
    swaggerLocation = "api/openapi-spec/swagger.json"
}
