package org.jenkinsci.plugins.postbuildscript.service;

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import java.io.IOException;
import org.jenkinsci.plugins.postbuildscript.PostBuildScriptException;

public class Content {

    private final FileCallable<String> callable;

    public Content(FileCallable<String> callable) {
        this.callable = callable;
    }

    public String resolve(FilePath filePath) throws PostBuildScriptException {
        try {
            return filePath.act(callable);
        } catch (IOException | InterruptedException ioe) {
            throw new PostBuildScriptException("Error calling file", ioe);
        }
    }
}
