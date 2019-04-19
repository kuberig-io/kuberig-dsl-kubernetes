package eu.rigeldev.kuberig.dsl.kubernetes;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.util.Map;

public class BintrayRemoveTask extends DefaultTask {

    private final String subject = "teyckmans";
    private final String repo = "rigeldev-oss-maven";

    @TaskAction
    public void bintrayRemove() throws Exception {
        UnirestConfigurator.configureUnirest();

        final Project rootProject = this.getProject();

        final String bintrayUser = (String) this.getProject().getProperties().get("bintrayUser");
        final String bintrayApiKey = (String) this.getProject().getProperties().get("bintrayApiKey");

        for (Map.Entry<String, Project> projectEntry : rootProject.getChildProjects().entrySet()) {
            final Project childProject = projectEntry.getValue();

            final String packageName = childProject.getName();
            final String version = childProject.getVersion().toString();

            System.out.println("Removing package: " + packageName + ", version: " + version);

            // DELETE /packages/:subject/:repo/:package/versions/:version
            final HttpResponse<String> deleteResponse = Unirest.delete("https://api.bintray.com/packages/{subject}/{repo}/{package}/versions/{version}")
                    .routeParam("subject", this.subject)
                    .routeParam("repo", this.repo)
                    .routeParam("package", packageName)
                    .routeParam("version", version)
                    .basicAuth(bintrayUser, bintrayApiKey)
                    .asString();

            if (deleteResponse.getStatus() != 200) {
                System.out.println("Failed to remove package: " + packageName + ", version: " + version);
            }
        }

    }

}
