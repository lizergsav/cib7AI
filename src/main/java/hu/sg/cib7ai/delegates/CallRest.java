package hu.sg.cib7ai.delegates;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.sg.cib7ai.config.WorkflowParameters;
import lombok.extern.slf4j.Slf4j;
import org.cibseven.bpm.engine.delegate.BpmnError;
import org.cibseven.bpm.engine.delegate.DelegateExecution;
import org.cibseven.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static hu.sg.cib7ai.config.WorkflowParameters.*;

@Slf4j
@Service
public class CallRest implements JavaDelegate {

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    public CallRest(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        Object apiName = execution.getVariable(REST_API_NAME);
        Object authType = execution.getVariable(REST_AUTH_TYPE);
        Object bpmnErrorParam = execution.getVariable(BPMN_ERROR_CODE);
        boolean bpmnError = bpmnErrorParam instanceof Boolean && (Boolean) bpmnErrorParam;

        if ( apiName instanceof String && !((String) apiName).isEmpty()) {
            /*
            if (execution.getVariable(REST_USERNAME) != null && execution.getVariable(REST_PASSWORD) != null) {
                try {
                    String username = (String)execution.getVariable(REST_USERNAME);
                    String password = (String)execution.getVariable(REST_PASSWORD);
                    username = helper(apiName.toString(), username, execution.getTenantId());
                    password = helper(apiName.toString(), password, execution.getTenantId());
                    if ( authType != null && authType.equals(REST_AUTH_PARAM)) {
                        String authUrl = helper(((String) apiName).trim(),REST_AUTH_URL, execution.getTenantId());
                        String secret = helper(((String) apiName).trim(),REST_AUTH_SECRET, execution.getTenantId());
                        String clientId = helper(((String) apiName).trim(),REST_AUTH_CLIENT_ID, execution.getTenantId());
                        String token = requestNewAuthToken(authUrl, clientId, secret, username, password);
                        if ( token != null)
                            headers.setBearerAuth(token);
                        else {
                            log.error("Error during get token, user: {}, pass: {}",username, password);
                            LoggingService.error("Error during CallRest delegate at auth2 authentication");
                            if ( bpmnError)
                                throw new Exception("Error during CallRest delegate at auth2 authentication");
                            else {
                                execution.setVariable(BPMN_ERROR_CODE, null);
                                throw new RuntimeException("Error during CallRest delegate at auth2 authentication");
                            }
                        }
                    } else
                        headers.setBasicAuth(username, password);
                } catch (Exception e) {
                    log.error("Error during get rest parameters, user: {}, pass: {}",execution.getVariable(REST_USERNAME), execution.getVariable(REST_PASSWORD));
                    LoggingService.error(e.getMessage());
                    if ( bpmnError)
                        throw new BpmnError(BPMN_ERROR_CODE,e.getMessage());
                    else {
                        execution.setVariable(BPMN_ERROR_CODE, null);
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
            */
        }

        String type = (String)execution.getVariable(REST_TYPE);
        String url = (String)execution.getVariable(REST_URL);
        Map<String, Object> param = mapper.convertValue(execution.getVariable(REST_PARAM), new TypeReference<>() {});
        Object data = execution.getVariable(REST_BODY);
        HttpEntity<Object> entity = new HttpEntity<>(data, headers);
        try {
            ResponseEntity<String> response;
            if ( param != null)
                response = restTemplate.exchange(url, HttpMethod.valueOf(type),entity,String.class,param);
            else
                response = restTemplate.exchange(url, HttpMethod.valueOf(type),entity,String.class);

            execution.setVariable(WorkflowParameters.ERROR_MESSAGE, response.getStatusCode());
            execution.setVariable(WorkflowParameters.SUCCESS, response.getStatusCode().is2xxSuccessful());
            log.debug("The rest call was sent params: type {}, url {}, data {}, params {} and the result is {}, body {}",
                    type, url, data, param, response.getStatusCode(), response.getBody());
            if (response.hasBody())
                execution.setVariable(REST_BODY, response.getBody());
        } catch (Exception e) {
            log.error("Error during rest call : {}, url is: {}", e.getMessage(), url);
            execution.setVariable(WorkflowParameters.ERROR_MESSAGE, e.getMessage());
            execution.setVariable(WorkflowParameters.SUCCESS, false);
           log.error(e.getMessage());
            if ( bpmnError)
                throw new BpmnError(BPMN_ERROR_CODE,e.getMessage());
            else {
                execution.setVariable(BPMN_ERROR_CODE, null);
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}

