package com.coreoz.plume.file;

import com.coreoz.plume.file.services.mimetype.MimeTypesDetector;
import com.coreoz.plume.file.validator.FileUploadSizeValidator;
import com.coreoz.plume.file.validator.FileUploadValidator;
import com.coreoz.plume.jersey.errors.WsException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileUploadValidatorTest {

    private FileUploadValidator makeValidator(String fileName, long size) {
        return (FileUploadValidator) makeClassicValidator(fileName, size);
    }

    private FileUploadSizeValidator makeClassicValidator(String fileName, long size) {
        return FileUploadValidator.from(
            fileName,
            size,
            InputStream.nullInputStream(),
            new MimeTypesDetector()
        );
    }

    @Test
    public void from__verify_general_workflow_without_errors() {
        makeClassicValidator("test.txt", 100)
            .fileMaxSize(100)
            .fileNameNotEmpty()
            .fileNameMaxDefaultLength()
            .fileExtensionNotEmpty()
            .fileExtensionMaxDefaultLength()
            .keepOriginalFileName()
            .finish();
    }

    @Test
    public void from__verify_permissive_workflow_without_errors() {
        makeClassicValidator(null, 100)
            .fileMaxSize(100)
            .fileNameAllowEmpty()
            .fileNameMaxDefaultLength()
            .fileExtensionAllowEmpty()
            .fileExtensionMaxDefaultLength()
            .sanitizeFileName()
            .finish();
    }

    @Test
    public void fileExtension__accepted_extension_should_not_fail() {
        makeValidator("test.xml", 0).fileExtensions(Set.of("xml"));
    }

    @Test
    public void fileExtension__not_accepted_extension_should_fail() {
        assertThrows(
            WsException.class,
            () -> makeValidator("test.xml", 0).fileExtensions(Set.of("xlsx"))
        );
    }

    @Test
    public void fileExtensionMaxLength__limit_length_should_pass() {
        makeValidator("test.abcdefgh", 0).fileExtensionMaxLength(8);
    }

    @Test
    public void fileExtensionMaxLength__over_limit_length_should_fail() {
        assertThrows(
            WsException.class,
            () -> makeValidator("test.abcdefgh", 0).fileExtensionMaxLength(7)
        );
    }

    @Test
    public void fileImage__should_fail_for_xml() {
        assertThrows(
            WsException.class,
            () -> makeValidator("test.xml", 0).fileImage()
        );

    }

    @Test
    public void fileImage__should_pass_for_jpg() {
        makeValidator("test.jpg", 0).fileImage();
    }

    @Test
    public void mimeTypes__xml_for_xml_should_pass() {
        makeValidator("test.xml", 0).mimeTypes(Set.of("application/xml"));
    }

    @Test
    public void mimeTypes__csv_for_csv_should_pass() {
        makeValidator("test.csv", 0).mimeTypes(Set.of("text/csv"));
    }

    @Test
    public void mimeTypes__xml_for_json_should_fail() {
        assertThrows(
            WsException.class,
            () -> makeValidator("test.xml", 0).mimeTypes(Set.of("application/json"))
        );
    }

    @Test
    public void fileSize__limit_size_should_pass() {
        makeValidator(null, 10).fileMaxSize(10);
    }

    @Test
    public void fileSize__over_limit_size_should_fail() {
        assertThrows(
            WsException.class,
            () -> makeValidator(null, 10).fileMaxSize(9)
        );
    }

    @Test
    public void fileNameMaxLength__limit_length_should_pass() {
        makeValidator("abcdefgh", 0).fileNameMaxLength(8);
    }

    @Test
    public void fileNameMaxLength__over_length_size_should_fail() {
        ;
        assertThrows(
            WsException.class,
            () -> makeValidator("abcdefgh", 0).fileNameMaxLength(7)
        );
    }

    @Test
    public void sanitizeOriginalFileName__should_return_accents_string_without_accents() {
        String fileName = makeValidator("éçàyt.jpg", 0)
            .sanitizeFileName()
            .finish()
            .getFileName();

        Assertions.assertThat(fileName).isEqualTo("ecayt.jpg");
    }

    @Test
    public void keepOriginalFileName__should_keep_original_name() {
        String fileName = makeValidator("éçàyt.jpg", 0)
            .keepOriginalFileName()
            .finish()
            .getFileName();

        Assertions.assertThat(fileName).isEqualTo("éçàyt.jpg");
    }

    @Test
    public void mapOriginalFileName__should_transform_original_name() {
        String fileName = makeValidator("éçàyt.jpg", 0)
            .changeFileName(filename -> "test_" + filename)
            .finish()
            .getFileName();

        Assertions.assertThat(fileName).isEqualTo("test_éçàyt.jpg");
    }
}
