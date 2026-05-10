package hu.sg.cib7ai.delegates;

import hu.sg.cib7ai.services.DocumentTextExtractorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cibseven.bpm.engine.delegate.DelegateExecution;
import org.cibseven.bpm.engine.delegate.JavaDelegate;
import org.cibseven.bpm.engine.variable.value.FileValue;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import static hu.sg.cib7ai.config.WorkflowParameters.DOCUMENT_UPLOAD_NAME;
import static hu.sg.cib7ai.config.WorkflowParameters.DOCUMENT_UPLOAD_RESULT_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractDocumentTextDelegate implements JavaDelegate {

    private final DocumentTextExtractorService documentTextExtractorService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        FileValue fileValue = execution.getVariableTyped(DOCUMENT_UPLOAD_NAME) != null ? execution.getVariableTyped(DOCUMENT_UPLOAD_NAME) : execution.getVariableTyped("doc");

        if (fileValue == null) {
            throw new RuntimeException("Process variable 'doc' is missing");
        }

        log.info("Processing uploaded file: {}", fileValue.getFilename());

        try (InputStream inputStream = fileValue.getValue()) {

            String extractedText =
                    documentTextExtractorService.extractText(inputStream);

            execution.setVariable(DOCUMENT_UPLOAD_RESULT_NAME, extractedText);

            log.info("Document text extracted successfully");
        }
    }
}
