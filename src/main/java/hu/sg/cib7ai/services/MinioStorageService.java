package hu.sg.cib7ai.services;

import hu.sg.cib7ai.dto.DocumentType;
import hu.sg.cib7ai.dto.MinioDocumentDto;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private final MinioClient minioClient;

    public String upload(
            String bucket,
            String filename,
            byte[] inputContent,
            DocumentType documentType,
            Map<String, String> metadata
    ) {

        try {

            String objectName =
                    UUID.randomUUID() + "_" + filename;

            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucket)
                            .build()
            );

            if (!bucketExists) {

                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucket)
                                .build()
                );
            }

            String contentType =
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            if ( documentType.equals(DocumentType.DOCX))
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            else {
                contentType = "application/octet-stream";
            }

            PutObjectArgs.Builder putBuilder =
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(
                                    new ByteArrayInputStream(inputContent),
                                    inputContent.length,
                                    -1
                            )
                            .contentType(contentType);
            if (metadata != null && !metadata.isEmpty()) {
                putBuilder.userMetadata(metadata);
            }

            minioClient.putObject(
                    putBuilder.build()
            );

            return objectName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to MinIO", e);
        }
    }

    public MinioDocumentDto get(
            String bucket,
            String objectName
    ) {

        try {

            StatObjectResponse stat =
                    minioClient.statObject(
                            StatObjectArgs.builder()
                                    .bucket(bucket)
                                    .object(objectName)
                                    .build()
                    );

            InputStream inputStream =
                    minioClient.getObject(
                            GetObjectArgs.builder()
                                    .bucket(bucket)
                                    .object(objectName)
                                    .build()
                    );

            byte[] content =
                    inputStream.readAllBytes();

            return MinioDocumentDto.builder()
                    .objectName(objectName)
                    .content(content)
                    .contentType(stat.contentType())
                    .size(stat.size())
                    .metadata(stat.userMetadata())
                    .build();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to get object from MinIO",
                    e
            );
        }
    }

    public void updateMetadata(
            String bucket,
            String objectName,
            Map<String, String> metadata
    ) {

        try {

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .source(
                                    CopySource.builder()
                                            .bucket(bucket)
                                            .object(objectName)
                                            .build()
                            )
                            .userMetadata(metadata)
                            .metadataDirective(
                                    Directive.REPLACE
                            )
                            .build()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update metadata",
                    e
            );
        }
    }

}
