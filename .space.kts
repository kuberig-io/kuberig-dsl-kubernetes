job("kuberig-dsl-kubernetes::new-upstream-version-job") {
    startOn {
        // every hour
        schedule { cron("0 * * * *") }
        gitPush { enabled = false }
    }

    container("openjdk:11") {
        env["ORG_GRADLE_PROJECT_bintrayApiKey"] = Secrets("BINTRAY_API_KEY")
        env["ORG_GRADLE_PROJECT_bintrayUser"] = Secrets("BINTRAY_USER")
        env["ORG_GRADLE_PROJECT_gradle.publish.key"] = Secrets("GRADLE_PUBLISH_KEY")
        env["ORG_GRADLE_PROJECT_gradle.publish.secret"] = Secrets("GRADLE_PUBLISH_SECRET")

        kotlinScript { api ->
            if (api.gitBranch() == "refs/heads/master") {
                api.gradlew("generateDslProjects")
                api.gradlew("showMissingFromJCenter")
                api.gradlew("commitAndPushMissing")
                api.gradlew("publishMissing")
            } else {
                println("Not on master skipping.")
            }
        }
    }
}