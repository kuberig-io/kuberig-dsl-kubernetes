package eu.rigeldev.kuberig.dsl.kubernetes;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;
import org.gradle.process.internal.ExecAction;
import org.gradle.process.internal.ExecActionFactory;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;

public class GitCommitAndPushMissing extends DefaultTask {

    @Inject
    public ExecActionFactory getExecActionFactory() { throw new UnsupportedOperationException(); }

    @TaskAction
    public void execute() {
        ExecActionFactory execActionFactory = this.getExecActionFactory();

        ExecAction gitAddAll = execActionFactory.newExecAction();
        gitAddAll.commandLine("git", "add", "--all")
                .setStandardOutput(System.out);

        gitAddAll.commandLine("git", "add", "--all");
        gitAddAll.execute();


        final ByteArrayOutputStream standardOutputStream = new ByteArrayOutputStream();

        ExecAction gitStatus = execActionFactory.newExecAction();
        gitStatus.commandLine("git", "status")
                .setStandardOutput(standardOutputStream);
        gitStatus.execute();

        final String gitStatusOutputText = standardOutputStream.toString();

        if (gitStatusOutputText.contains("added") || gitStatusOutputText.contains("modified")) {
            if (gitStatusOutputText.contains("new file:") || gitStatusOutputText.contains("modified:")) {
                ExecAction gitCommit = execActionFactory.newExecAction();
                gitCommit.commandLine("git", "commit", "-m", "\"Auto commit to add new upstream versions.\"");
                ExecResult gitCommitResult = gitCommit.execute();
                gitCommitResult.assertNormalExitValue();

                ExecAction gitPush = execActionFactory.newExecAction();
                gitPush.commandLine("git", "push", "origin");
                ExecResult gitPushResult = gitPush.execute();
                gitPushResult.assertNormalExitValue();
            } else {
                this.getLogger().info("Nothing to commit and push");
            }
        }

    }
}
