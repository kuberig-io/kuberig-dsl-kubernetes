package eu.rigeldev.kuberig.dsl.kubernetes;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DslProjectsGeneratorPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {

        project.getTasks().register("generateDslProjects", DslProjectsGeneratorTask.class, task ->
                task.setGroup("kuberig")
        );

    }
}
