package hu.sg.cib7ai.services;

import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
@Service
public class DocxToPdfService {

    public byte[] convert(byte[] docxBytes) {

        try (
                ByteArrayInputStream inputStream =
                        new ByteArrayInputStream(docxBytes);

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            WordprocessingMLPackage wordMLPackage =
                    WordprocessingMLPackage.load(inputStream);

            Docx4J.toPDF(wordMLPackage, outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {

            log.error("DOCX to PDF conversion failed", e);

            throw new RuntimeException(
                    "Failed to conver   t DOCX to PDF",
                    e
            );
        }
    }
}