package eu.rigeldev.kuberig.dsl.kubernetes;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class DslProjectsGeneratorTask extends DefaultTask {

    private String gitHubOwner = "kubernetes";
    private String gitHubRepo = "kubernetes";

    @TaskAction
    public void generatorDslProjects() throws Exception {
        UnirestConfigurator.configureUnirest();

        final HttpResponse<GitHubTagRef[]> tags = Unirest.get("https://api.github.com/repos/{gitHubOwner}/{gitHubRepo}/git/refs/tags")
                .routeParam("gitHubOwner", this.gitHubOwner)
                .routeParam("gitHubRepo", this.gitHubRepo)
                .header("Accept", "application/vnd.github.v3+json")
                .asObject(GitHubTagRef[].class);

        boolean passedInitialTag = false;

        for (GitHubTagRef tag : tags.getBody()) {


            final String tagName = tag.getRef().substring("refs/tags/".length());

            if (tagName.equals("v1.10.7")) {
                passedInitialTag = true;
            }

            if (passedInitialTag) {

                System.out.println("Generating project for tag " + tagName);

                String moduleName = "kuberig-dsl-kubernetes-" + tagName;
                File moduleDir = new File(moduleName);

                if (!this.isModuleValid(moduleDir)) {

                    this.createDirectoryIfNeeded(moduleDir);
                    this.createDirectoryIfNeeded(new File(moduleDir, "src"));
                    this.createDirectoryIfNeeded(new File(moduleDir, "src/main"));
                    this.createDirectoryIfNeeded(new File(moduleDir, "src/main/resources"));

                    final GetRequest getRequest = Unirest.get("https://raw.githubusercontent.com/{gitHubOwner}/{gitHubRepo}/{tagName}/api/openapi-spec/swagger.json")
                            .routeParam("gitHubOwner", this.gitHubOwner)
                            .routeParam("gitHubRepo", this.gitHubRepo)
                            .routeParam("tagName", tagName);
                    final String swaggerJsonUrl = getRequest.getUrl();
                    final HttpResponse<String> swaggerJson = getRequest
                            .asString();

                    if (swaggerJson.getStatus() == 200) {
                        final String swaggerJsonText = swaggerJson.getBody();
                        if (swaggerJsonText.contains("x-kubernetes-group-version-kind")) {
                            System.out.println(tagName + " => VALID ");

                            File swaggerJsonFile = new File(moduleDir, "src/main/resources/swagger.json");
                            Files.write(swaggerJsonFile.toPath(), swaggerJsonText.getBytes(StandardCharsets.UTF_8));

                            File buildGradleKtsFile = new File(moduleDir, "build.gradle.kts");
                            List<String> buildGradleKtsLines = Arrays.asList("plugins {",
                                    "    id(\"eu.rigeldev.kuberig.dsl.generator\") version \""+getProject().getVersion().toString()+"\"",
                                    "}",
                                    "",
                                    "repositories {",
                                    "    jcenter()",
                                    "}");
                            Files.write(buildGradleKtsFile.toPath(), buildGradleKtsLines);


                            File readmeFile = new File(moduleDir, "README.MD");
                            List<String> readmeLines = Arrays.asList(
                                    "# " + moduleName,
                                    "",
                                    "Swagger file downloaded from " + swaggerJsonUrl
                            );
                            Files.write(readmeFile.toPath(), readmeLines, StandardCharsets.UTF_8);

                            Path settingsGradleKts = Paths.get("settings.gradle.kts");

                            final List<String> settingsGradleKtsLines = Files.readAllLines(settingsGradleKts, StandardCharsets.UTF_8);

                            final String lineToAdd = "\ninclude(\"" + moduleName + "\")";

                            if (!settingsGradleKtsLines.contains(lineToAdd)) {
                                Files.write(settingsGradleKts, lineToAdd.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                            }

                        } else {
                            System.out.println(tagName + " => IN-VALID ( does not have x-kubernetes-group-version-kind info )");
                        }
                    } else {
                        System.out.println(tagName + " => " + swaggerJson.getStatusText());
                    }
                } else {
                    File buildGradleKtsFile = new File(moduleDir, "build.gradle.kts");

                    List<String> buildGradleKtsLines = Arrays.asList("plugins {",
                            "    id(\"eu.rigeldev.kuberig.dsl.generator\") version \""+getProject().getVersion().toString()+"\"",
                            "}",
                            "",
                            "repositories {",
                            "    jcenter()",
                            "}");

                    Files.write(buildGradleKtsFile.toPath(), buildGradleKtsLines);
                }
            }
        }

    }

    private boolean isModuleValid(File moduleDir) {
        boolean moduleDirectoryExists = moduleDir.exists();

        final File buildFile = new File(moduleDir, "build.gradle.kts");

        boolean validBuildFile = buildFile.exists() && buildFile.length() != 0;

        final File swaggerFile = new File(moduleDir, "src/main/resources/swagger.json");

        boolean validSwaggerFile = swaggerFile.exists() && swaggerFile.length() != 0;

        return moduleDirectoryExists && validBuildFile && validSwaggerFile;
    }

    private void createDirectoryIfNeeded(File directory) {
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw new IllegalStateException("Failed to create directory [" + directory.getAbsolutePath() + "]");
            }
        }
    }

}
