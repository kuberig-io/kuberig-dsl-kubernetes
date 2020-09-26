package eu.rigeldev.kuberig.dsl.kubernetes;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DslProjectsGeneratorPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {

        project.getTasks().register("generateDslProjects", DslProjectsGeneratorTask.class, this::kuberigTask);
        project.getTasks().register("bintrayRemove", BintrayRemoveTask.class, this::kuberigTask);
        project.getTasks().register("showMissingFromJCenter", ShowMissingFromJCenterTask.class, this::kuberigTask);
        project.getTasks().register("commitAndPushMissing", GitCommitAndPushMissing.class, this::kuberigTask);

        project.getTasks().register(
                "publishMissing",
                PublishMissing.class,
                task -> {
                    this.kuberigTask(task);
                    final String version = project.getVersion().toString();

                    final Set<Project> subProjects = project.getSubprojects();

                    final List<Project> subProjectsToPublish = new ArrayList<>();

                    for (Project subProject : subProjects) {
                        if (!JCenterUtils.exists(subProject.getName(), version)) {
                            subProjectsToPublish.add(subProject);
                        }
                    }

                    if (subProjectsToPublish.isEmpty()) {
                        project.getLogger().warn("Nothing missing to publish.");
                    } else {
                        for (Project subProjectToPublish : subProjectsToPublish) {
                            project.getLogger().info(subProjectToPublish.getName() + " needs to be published.");
                            task.dependsOn(
                                    subProjectToPublish.getTasks().getByName("bintrayUpload")
                            );
                        }
                    }
                }
        );
    }

    private <T extends DefaultTask> void kuberigTask(T task) {
        task.setGroup("kuberig");
    }
}
