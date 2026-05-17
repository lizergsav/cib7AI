package hu.sg.cib7ai.services;

import hu.sg.cib7ai.dto.DocumentType;

import java.util.Locale;

public class Utils {

    public static String textNormalizer(String text) {
        if (text == null) {
            return "";
        }

        return text
                .toLowerCase(Locale.forLanguageTag("hu"))
                .trim()
                .replaceAll("[^a-z0-9áéíóöőúüű\\s]", "")
                .replaceAll("\\s+", " ");
    }

    public static DocumentType detectDocumentType(
            String filename
    ) {

        if (filename == null) {
            return DocumentType.OTHER;
        }

        String lower =
                filename.trim().toLowerCase();

        if (lower.endsWith(".docx")) {
            return DocumentType.DOCX;
        }

        if (lower.endsWith(".pdf")) {
            return DocumentType.PDF;
        }

        if (
                lower.endsWith(".png") ||
                        lower.endsWith(".jpg") ||
                        lower.endsWith(".jpeg") ||
                        lower.endsWith(".gif") ||
                        lower.endsWith(".bmp") ||
                        lower.endsWith(".webp")
        ) {
            return DocumentType.PICTURE;
        }

        return DocumentType.OTHER;
    }

}
