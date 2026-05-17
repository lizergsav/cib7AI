package hu.sg.cib7ai.controller;

import hu.sg.cib7ai.dto.MinioDocumentDto;
import hu.sg.cib7ai.services.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/minio")
public class MinioController {

    private final MinioStorageService minioService;

    @GetMapping("/{bucket}/{objectName}")
    public ResponseEntity<byte[]> getDocument(
            @PathVariable String bucket,
            @PathVariable String objectName
    ) {

        MinioDocumentDto document =
                minioService.get(bucket, objectName);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                document.getContentType()
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                document.getObjectName() +
                                "\""
                )

                // metadata headers
                .headers(headers -> {
                    if (document.getMetadata() != null) {

                        document.getMetadata()
                                .forEach((key, value) ->
                                        headers.add(
                                                "X-Meta-" + key,
                                                value
                                        )
                                );
                    }
                })

                .body(document.getContent());
    }
}
