package hu.sg.cib7ai.delegates;

import hu.sg.cib7ai.dto.DocumentType;
import hu.sg.cib7ai.services.DocxToPdfService;
import hu.sg.cib7ai.services.MinioStorageService;
import hu.sg.cib7ai.services.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cibseven.bpm.engine.delegate.DelegateExecution;
import org.cibseven.bpm.engine.delegate.JavaDelegate;
import org.cibseven.bpm.engine.variable.value.FileValue;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static hu.sg.cib7ai.config.WorkflowParameters.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadDocumentDelegate implements JavaDelegate {

    private final DocxToPdfService docxToPdfService;
    private final MinioStorageService minioService;
    private final String BUCKET_NAME = "merkantil";

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        FileValue fileValue = execution.getVariableTyped(DOCUMENT_UPLOAD_NAME) != null ? execution.getVariableTyped(DOCUMENT_UPLOAD_NAME) : execution.getVariableTyped("doc");
        DocumentType docType = DocumentType.from(
                Objects.toString(
                        execution.getVariable(DOCUMENT_DOC_TYPE),
                        null
                )
        );

        //String existingDocId = execution.getVariable(DOCUMENT_MINIO_OBJECT_ID) != null ? execution.getVariable(DOCUMENT_MINIO_OBJECT_ID).toString() : null;

        if (fileValue == null) {
            throw new RuntimeException("Process variable 'doc' is missing");
        }

        log.info("Processing uploaded file: {}", fileValue.getFilename());
        Map<String, String> metadata = new HashMap<>();
        metadata.put("filename", fileValue.getFilename());

        try (InputStream inputStream = fileValue.getValue()) {

            byte[] content = inputStream.readAllBytes();

            String uniqueFilename =
                    System.currentTimeMillis() +
                            "_" +
                            (
                                    fileValue.getFilename() == null
                                            ? "document"
                                            : fileValue.getFilename().trim()
                            );

            String docId = minioService.upload(BUCKET_NAME,
                    uniqueFilename,
                    content,
                    docType,
                    metadata);

            /*
            if ( docType.equals(DocumentType.DOCX)) {
                // we need to convert and upload
                content = docxToPdfService.convert(content);
                docId = minioService.upload(BUCKET_NAME,
                        "pdf".concat(uniqueFilename),
                        content,
                        docType,
                        metadata);
            }
             */

            execution.setVariable(DOCUMENT_MINIO_OBJECT_ID,docId);

            log.info("Document text extracted successfully");
        }
    }

}
