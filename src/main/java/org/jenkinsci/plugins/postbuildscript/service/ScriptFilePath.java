package org.jenkinsci.plugins.postbuildscript.service;

import hudson.FilePath;
import java.io.IOException;
import org.jenkinsci.plugins.postbuildscript.Messages;
import org.jenkinsci.plugins.postbuildscript.PostBuildScriptException;

public class ScriptFilePath {

    private final FilePath workspace;

    public ScriptFilePath(FilePath workspace) {
        this.workspace = workspace;
    }

    public FilePath resolve(String command) throws PostBuildScriptException {

        FilePath filePath = getFilePath(command);
        if (filePath == null) {
            throw new PostBuildScriptException(Messages.PostBuildScript_ScriptFilePathDoesNotExist(command));
        }

        return filePath;
    }

    private FilePath getFilePath(String givenPath) throws PostBuildScriptException {

        try {
            return workspace.act(new LoadFileCallable(givenPath, workspace));
        } catch (IOException | InterruptedException ioe) {
            throw new PostBuildScriptException("Error to resolve script path", ioe);
        }
    }
}
