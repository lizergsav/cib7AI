package hu.sg.cib7ai.delegates;

import hu.sg.cib7ai.config.WorkflowParameters;
import lombok.extern.slf4j.Slf4j;
import org.cibseven.bpm.engine.delegate.DelegateExecution;
import org.cibseven.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class Debug implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Object logValue = delegateExecution.getVariable(WorkflowParameters.DEBUG_VARIABLE_NAME);
        String elementId = delegateExecution.getCurrentActivityId();
        String elementName = delegateExecution.getCurrentActivityName();
        log.debug("Element id: {}, name: {}", elementId, elementName);

        if (log.isDebugEnabled()) {
            if (logValue instanceof String && !((String) logValue).isEmpty()) {
                log.debug("#######################");
                log.debug("Logging {} variable the value is: {}", logValue, delegateExecution.getVariable(logValue.toString()));
                log.debug("#######################");
            }

            Map<String, Object> variables = delegateExecution.getVariables();
            log.debug("Logging all process variables:");
            variables.forEach((key, value) -> log.debug("{} = {}", key, value));
        }
    }
}
