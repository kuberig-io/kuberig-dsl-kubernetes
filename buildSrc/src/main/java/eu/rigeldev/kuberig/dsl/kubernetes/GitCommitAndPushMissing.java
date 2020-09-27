package eu.rigeldev.kuberig.dsl.kubernetes;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.LogLevel;
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

        ExecAction gitConfigUserEmail = execActionFactory.newExecAction();
        gitConfigUserEmail.commandLine("git", "config", "user.email", "\"space.automation@rigel.dev\"");
        gitConfigUserEmail.setStandardOutput(new LogOutputStream(this.getLogger(), LogLevel.INFO));
        gitConfigUserEmail.setErrorOutput(new LogOutputStream(this.getLogger(), LogLevel.ERROR));
        this.getLogger().info("Configuring git user.email");
        ExecResult gitConfigUserEmailResult = gitConfigUserEmail.execute();
        gitConfigUserEmailResult.assertNormalExitValue();

        ExecAction gitConfigUserName = execActionFactory.newExecAction();
        gitConfigUserName.commandLine("git", "config", "user.name", "\"Space Automation\"");
        gitConfigUserName.setStandardOutput(new LogOutputStream(this.getLogger(), LogLevel.INFO));
        gitConfigUserName.setErrorOutput(new LogOutputStream(this.getLogger(), LogLevel.ERROR));
        this.getLogger().info("Configuring git user.name...");
        ExecResult gitConfigUserNameResult = gitConfigUserEmail.execute();
        gitConfigUserNameResult.assertNormalExitValue();

        ExecAction gitAddAll = execActionFactory.newExecAction();
        gitAddAll.commandLine("git", "add", "--all");
        gitAddAll.setStandardOutput(new LogOutputStream(this.getLogger(), LogLevel.INFO));
        gitAddAll.setErrorOutput(new LogOutputStream(this.getLogger(), LogLevel.ERROR));
        this.getLogger().info("Executing git add --all...");
        gitAddAll.execute();


        final ByteArrayOutputStream standardOutputStream = new ByteArrayOutputStream();

        ExecAction gitStatus = execActionFactory.newExecAction();
        gitStatus.commandLine("git", "status")
                .setStandardOutput(standardOutputStream);
        gitStatus.setErrorOutput(new LogOutputStream(this.getLogger(), LogLevel.ERROR));
        this.getLogger().info("Running git status...");
        gitStatus.execute();

        final String gitStatusOutputText = standardOutputStream.toString();
        this.getLogger().info(gitStatusOutputText);

        if (gitStatusOutputText.contains("added") || gitStatusOutputText.contains("modified")) {
            if (gitStatusOutputText.contains("new file:") || gitStatusOutputText.contains("modified:")) {
                ExecAction gitCommit = execActionFactory.newExecAction();
                gitCommit.commandLine("git", "commit", "-m", "\"Auto commit to add new upstream versions.\"");
                gitCommit.setStandardOutput(new LogOutputStream(this.getLogger(), LogLevel.INFO));
                gitCommit.setErrorOutput(new LogOutputStream(this.getLogger(), LogLevel.ERROR));
                ExecResult gitCommitResult = gitCommit.execute();
                gitCommitResult.assertNormalExitValue();

                ExecAction gitPush = execActionFactory.newExecAction();
                gitPush.commandLine("git", "push", "origin", "master");
                gitPush.setStandardOutput(new LogOutputStream(this.getLogger(), LogLevel.INFO));
                gitPush.setErrorOutput(new LogOutputStream(this.getLogger(), LogLevel.ERROR));
                ExecResult gitPushResult = gitPush.execute();
                gitPushResult.assertNormalExitValue();
            } else {
                this.getLogger().info("Nothing to commit and push");
            }
        }

    }
}
