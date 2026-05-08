package hu.sg.cib7ai;

import hu.sg.cib7ai.services.DocumentTextExtractorService;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentTextExtractorServiceTest {

    private DocumentTextExtractorService service;

    @BeforeEach
    void setUp() {
        Tika tika = new Tika();
        service = new DocumentTextExtractorService(tika);
    }

    @Test
    void shouldExtractTextFromPlainText() {

        String content = "Hello CIB7 AI";

        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        String extracted = service.extractText(inputStream);

        assertTrue(extracted.contains("Hello CIB7 AI"));
    }
}
