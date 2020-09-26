job("kuberig-dsl-kubernetes::new-upstream-version-job") {

    /*
    TODO schedule

    startOn {
        // every day at 05 AM UTC
        schedule { cron("0 5 * * *") }
    }
    */

    container("gradle:6.6.1-jdk11") {
        env["BINTRAY_API_KEY"] = Secrets("BINTRAY_API_KEY")
        env["BINTRAY_USER"] = Secrets("BINTRAY_USER")
        env["GRADLE_PUBLISH_KEY"] = Secrets("GRADLE_PUBLISH_KEY")
        env["GRADLE_PUBLISH_SECRET"] = Secrets("GRADLE_PUBLISH_SECRET")

        kotlinScript { api ->
            api.gradlew("generateDslProjects")
            api.gradlew("showMissingFromJCenter")
            api.gradlew("commitAndPushMissing")
            api.gradlew("publishMissing")
        }
    }
}