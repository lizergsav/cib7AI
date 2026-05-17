package hu.sg.cib7ai.dto;

public enum DocumentType {
    DOCX, PDF, PICTURE, OTHER;

    public static DocumentType from(String value) {

        if (value == null) {
            return OTHER;
        }

        try {
            return DocumentType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }

}
