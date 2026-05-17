package hu.sg.cib7ai.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MinioDocumentDto {

    private byte[] content;

    private String objectName;

    private String contentType;

    private long size;

    private Map<String, String> metadata;
}
