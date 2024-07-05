package org.jenkinsci.plugins.postbuildscript.service;

import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.postbuildscript.logging.Logger;
import org.jenkinsci.plugins.postbuildscript.model.Script;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GroovyScriptExecutorFactoryTest {

    @Mock
    private Logger logger;

    @Mock
    private AbstractBuild<?, ?> build;

    @Mock
    private Script script;

    @InjectMocks
    private GroovyScriptExecutorFactory executorFactory;

    @Test
    public void createsExecutor() {

        given(script.getContent()).willReturn("scriptContent");
        given(script.isSandboxed()).willReturn(true);

        GroovyScriptExecutor executor = executorFactory.create(script, Collections.emptyList());

        assertThat(executor, is(notNullValue()));

    }
}
