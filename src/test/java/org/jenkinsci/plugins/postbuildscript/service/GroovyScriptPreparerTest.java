package org.jenkinsci.plugins.postbuildscript.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import hudson.FilePath;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import org.jenkinsci.plugins.postbuildscript.Messages;
import org.jenkinsci.plugins.postbuildscript.logging.Logger;
import org.jenkinsci.plugins.postbuildscript.model.Script;
import org.jenkinsci.plugins.postbuildscript.model.ScriptFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GroovyScriptPreparerTest {

    private static final String SCRIPT_CONTENT = "scriptContent";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";

    @Mock
    private Logger logger;

    @Mock
    private GroovyScriptExecutorFactory executorFactory;

    @Mock
    private GroovyScriptExecutor executor;

    @Mock
    private Script script;

    @Mock
    private ScriptFile scriptFile;

    private GroovyScriptPreparer groovyScriptPreparer;

    private File file;

    public GroovyScriptPreparerTest() {}

    @BeforeEach
    public void initPreparer() throws URISyntaxException {
        file = new File(getClass().getResource("/test_script").toURI());
        FilePath workspace = new FilePath(file.getParentFile());
        groovyScriptPreparer = new GroovyScriptPreparer(logger, workspace, executorFactory);
    }

    @Test
    public void failsIfNoScriptContent() {

        assertThrows(IllegalArgumentException.class, () -> groovyScriptPreparer.evaluateScript(null));
    }

    @Test
    public void doesNotExecuteScriptIfWorkspaceIsNull() {
        given(script.getContent()).willReturn(SCRIPT_CONTENT);
        GroovyScriptPreparer groovyScriptPreparer = new GroovyScriptPreparer(logger, null, executorFactory);

        boolean evaluated = groovyScriptPreparer.evaluateScript(script);

        assertThat(evaluated, is(false));
    }

    @Test
    public void createsExecutorAndActsOnWorkspace() throws Exception {
        given(script.getContent()).willReturn(SCRIPT_CONTENT);
        given(executorFactory.create(script, Collections.emptyList())).willReturn(executor);

        boolean evaluated = groovyScriptPreparer.evaluateScript(script);

        assertThat(evaluated, is(true));
        verify(executor).execute();
    }

    @Test
    public void logsExecutionFailAndReturnsFalse() throws Exception {
        given(script.getContent()).willReturn(SCRIPT_CONTENT);
        given(executorFactory.create(script, Collections.emptyList())).willReturn(executor);
        Exception exception = new Exception(EXCEPTION_MESSAGE);
        willThrow(exception).given(executor).execute();

        boolean evaluated = groovyScriptPreparer.evaluateScript(script);

        assertThat(evaluated, is(false));
        verify(logger).info(Messages.PostBuildScript_ProblemOccured(), exception);
    }

    @Test
    public void evaluatesFile() throws Exception {

        given(executorFactory.create(any(Script.class), eq(Collections.emptyList())))
                .willReturn(executor);

        boolean evaluated = groovyScriptPreparer.evaluateCommand(scriptFile, new Command(file.getName()));

        verify(executorFactory).create(any(Script.class), eq(Collections.emptyList()));
        verify(executor).execute();
        assertThat(evaluated, is(true));
    }

    @Test
    public void doesNotExecuteScriptFileIfWorkspaceIsNull() throws Exception {

        GroovyScriptPreparer groovyScriptPreparer = new GroovyScriptPreparer(logger, null, executorFactory);

        boolean evaluated = groovyScriptPreparer.evaluateCommand(scriptFile, new Command(file.getName()));

        assertThat(evaluated, is(false));
    }
}
