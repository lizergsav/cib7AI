package hu.sg.cib7ai.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentTextExtractorService {

    private final Tika tika;

    /*
    public String extractText(InputStream inputStream) {
        try {
            return tika.parseToString(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract text from document", e);
        }
    }*/

    public String extractText(byte[] content) {

        try (InputStream inputStream =
                     new ByteArrayInputStream(content)) {

            return tika.parseToString(inputStream);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to extract text from document",
                    e
            );
        }
    }

}
