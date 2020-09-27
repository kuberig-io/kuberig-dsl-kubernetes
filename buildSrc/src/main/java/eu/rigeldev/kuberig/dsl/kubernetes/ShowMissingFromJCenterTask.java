package eu.rigeldev.kuberig.dsl.kubernetes;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

public class ShowMissingFromJCenterTask extends DefaultTask {

    public ShowMissingFromJCenterTask() {
        this.setGroup("kuberig");
    }

    @TaskAction
    public void showMissingFromJCenter() throws UnirestException, IOException {
        UnirestConfigurator.configureUnirest();

        final File statusFile = this.getProject().file("AVAILABILITY.MD");

        try (BufferedWriter writer = Files.newBufferedWriter(statusFile.toPath())) {
            writer.append("# Dependency Availability");
            writer.newLine();
            writer.append("| kubernetes version | repositories | bintray package |");
            writer.newLine();
            writer.append("| ------------------ | ------------ | --------------- |");
            writer.newLine();

            final Set<Project> subProjects = this.getProject().getSubprojects();

            for (Project subProject : subProjects) {
                HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.get("https://api.bintray.com/packages/teyckmans/rigeldev-oss-maven/{module}")
                        .routeParam("module", subProject.getName())
                        .asJson();

                String x = "["+subProject.getName()+"](https://bintray.com/teyckmans/rigeldev-oss-maven/" + subProject.getName() + ")";
                System.out.println(x);

                final String upstreamVersion = subProject.getName().substring("kuberig-dsl-kubernetes-".length());

                if (jsonNodeHttpResponse.getStatus() == 404) {
                    writer.append("|").append(subProject.getName()).append("|none|").append(x).append("|");
                    writer.newLine();
                } else {
                    JSONArray linkedToRepos = jsonNodeHttpResponse.getBody().getObject().getJSONArray("linked_to_repos");
                    if (linkedToRepos.length() == 0) {
                        writer.append("| ").append(upstreamVersion).append(" | rigeldev-oss-maven | ").append(x).append(" |");
                        writer.newLine();

                        System.out.println("available in [rigeldev-oss-maven]");
                        System.out.println("\t\tadd to jcenter with: " +
                                "https://bintray.com/message/addPackageToJCenter?pkgPath=%2Fteyckmans%2Frigeldev-oss-maven%2F" + subProject.getName() + "&tab=general"

                        );
                    } else {
                        writer.append("| ").append(upstreamVersion).append(" | rigeldev-oss-maven, jcenter | ").append(x).append(" |");
                        writer.newLine();
                    }
                }
            }

            System.out.println();
            System.out.println("This package contains a version number in the name that matches the upstream kubernetes version this package is intended for.");
            System.out.println();
        }

    }

}
