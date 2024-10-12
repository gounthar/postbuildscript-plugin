package org.jenkinsci.plugins.postbuildscript.service;

import hudson.FilePath;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.jenkinsci.plugins.postbuildscript.Messages;
import org.jenkinsci.plugins.postbuildscript.PostBuildScriptException;
import org.jenkinsci.plugins.postbuildscript.logging.Logger;
import org.jenkinsci.plugins.postbuildscript.model.Script;
import org.jenkinsci.plugins.postbuildscript.model.ScriptFile;

/**
 * @author Gregory Boissinot
 */
public class GroovyScriptPreparer implements Serializable {

    private static final long serialVersionUID = 6304738377691375266L;

    private final Logger logger;

    private final transient GroovyScriptExecutorFactory groovyScriptExecutorFactory;

    private final FilePath workspace;

    public GroovyScriptPreparer(
            Logger logger, FilePath workspace, GroovyScriptExecutorFactory groovyScriptExecutorFactory) {
        this.logger = logger;
        this.workspace = workspace;
        this.groovyScriptExecutorFactory = groovyScriptExecutorFactory;
    }

    public boolean evaluateScript(Script script) {
        return evaluateScript(script, Collections.emptyList());
    }

    public boolean evaluateScript(Script script, List<String> arguments) {

        if (script == null || script.getContent() == null) {
            throw new IllegalArgumentException("The script content object must be set.");
        }

        if (workspaceIsNull()) {
            return false;
        }

        try {
            GroovyScriptExecutor groovyScriptExecutor = groovyScriptExecutorFactory.create(script, arguments);
            groovyScriptExecutor.execute();
        } catch (Exception exception) {
            logger.info(Messages.PostBuildScript_ProblemOccured(), exception);
            return false;
        }

        return true;
    }

    private boolean workspaceIsNull() {
        if (workspace == null) {
            logger.info(Messages.PostBuildScript_WorkspaceEmpty());
            return true;
        }
        return false;
    }

    public boolean evaluateCommand(ScriptFile scriptFile, Command command) throws PostBuildScriptException {

        if (workspaceIsNull()) {
            return false;
        }

        FilePath filePath = new ScriptFilePath(workspace).resolve(command.getScriptPath());
        LoadScriptContentCallable callable = new LoadScriptContentCallable();
        String scriptContent = new Content(callable).resolve(filePath);
        Script script = new Script(scriptFile.getResults(), scriptContent);
        script.setSandboxed(scriptFile.isSandboxed());
        return evaluateScript(script, command.getParameters());
    }
}
