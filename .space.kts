job("kuberig-dsl-kubernetes::new-upstream-version-job") {

    /*
    TODO schedule

    startOn {
        // every day at 05 AM UTC
        schedule { cron("0 5 * * *") }
    }
    */

    container("gradle:6.6.1-jdk11") {
        env["ORG_GRADLE_PROJECT_bintrayApiKey"] = Secrets("BINTRAY_API_KEY")
        env["ORG_GRADLE_PROJECT_bintrayUser"] = Secrets("BINTRAY_USER")
        env["ORG_GRADLE_PROJECT_gradle.publish.key"] = Secrets("GRADLE_PUBLISH_KEY")
        env["ORG_GRADLE_PROJECT_gradle.publish.secret"] = Secrets("GRADLE_PUBLISH_SECRET")

        kotlinScript { api ->
            api.gradlew("generateDslProjects")
            api.gradlew("showMissingFromJCenter")
            api.gradlew("commitAndPushMissing")
            api.gradlew("publishMissing")
        }
    }
}