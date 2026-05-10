package hu.sg.cib7ai.delegates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cibseven.bpm.engine.delegate.DelegateExecution;
import org.cibseven.bpm.engine.delegate.JavaDelegate;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static hu.sg.cib7ai.config.WorkflowParameters.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentExtractionDelegate implements JavaDelegate {

    private final ChatClient chatClient;

    @Override
    public void execute(DelegateExecution execution) {

        String input = getRequiredVariable(execution);
        @SuppressWarnings("unchecked")
        List<String> keys =
                (List<String>) execution.getVariableLocal(AI_KEYS_NAME);

        String fieldList = keys.stream()
                .map(k -> "- " + k)
                .collect(Collectors.joining("\n"));

        String finalPrompt = """
        Extract the following fields from the document:

        %s

        Rules:
        - Return ONLY valid compact JSON
        - Do NOT use markdown
        - Do NOT wrap the response in ```json
        - Do NOT add explanations
        - Use null if a field is missing
        - Output must start with {
        - Output must end with }

        Document:
        %s
        """.formatted(fieldList, input);

        log.debug("Final prompt: {}", finalPrompt);

        String response = chatClient
                .prompt()
                .user(finalPrompt)
                .options(
                        OllamaOptions.builder()
                                .temperature(0.0)
                                .build()
                )
                .call()
                .content();

        log.info("LLM response: {}", response);

        execution.setVariable(AI_OUTPUT_NAME, response);
    }

    private String getRequiredVariable(DelegateExecution execution) {

        Object value = execution.getVariable(AI_INPUT_NAME);

        if (value == null) {
            throw new IllegalArgumentException(
                    "Missing required workflow variable: " + AI_INPUT_NAME);
        }

        return value.toString();
    }
}