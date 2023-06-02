package com.coreoz.plume.file;

import com.coreoz.plume.file.services.mimetype.MimeTypesDetector;
import com.coreoz.plume.file.validator.FileUploadSizeValidator;
import com.coreoz.plume.file.validator.FileUploadValidator;
import com.coreoz.plume.jersey.errors.WsException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Test;

import java.io.InputStream;
import java.util.Set;

public class FileUploadValidatorTest {
    private FormDataBodyPart makeBodyPart(String fileName, long size) {
        FormDataContentDisposition contentDisposition = FormDataContentDisposition.name("dummy unused name")
            .fileName(fileName)
            .size(size)
            .build();
        FormDataBodyPart bodyPart = new FormDataBodyPart();
        bodyPart.setFormDataContentDisposition(contentDisposition);
        return bodyPart;
    }

    private FileUploadValidator makeValidator(String fileName, long size) {
        return (FileUploadValidator) makeClassicValidator(fileName, size);
    }

    private FileUploadSizeValidator makeClassicValidator(String fileName, long size) {
        return FileUploadValidator.from(
            makeBodyPart(fileName, size),
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
            .finish();
    }

    @Test
    public void fileExtension__accepted_extension_should_not_fail() {
        makeValidator("test.xml", 0).fileExtensions(Set.of("xml"));
    }

    @Test(expected = WsException.class)
    public void fileExtension__not_accepted_extension_should_fail() {
        makeValidator("test.xml", 0).fileExtensions(Set.of("xlsx"));
    }

    @Test
    public void fileExtensionMaxLength__limit_length_should_pass() {
        makeValidator("test.abcdefgh", 0).fileExtensionMaxLength(8);
    }

    @Test(expected = WsException.class)
    public void fileExtensionMaxLength__over_limit_length_should_fail() {
        makeValidator("test.abcdefgh", 0).fileExtensionMaxLength(7);
    }

    @Test(expected = WsException.class)
    public void fileImage__should_fail_for_xml() {
        makeValidator("test.xml", 0).fileImage();
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

    @Test(expected = WsException.class)
    public void mimeTypes__xml_for_json_should_fail() {
        makeValidator("test.xml", 0).mimeTypes(Set.of("application/json"));
    }

    @Test
    public void fileSize__limit_size_should_pass() {
        makeValidator(null, 10).fileMaxSize(10);
    }

    @Test(expected = WsException.class)
    public void fileSize__over_limit_size_should_fail() {
        makeValidator(null, 10).fileMaxSize(9);
    }

    @Test
    public void fileNameMaxLength__limit_length_should_pass() {
        makeValidator("abcdefgh", 0).fileNameMaxLength(8);
    }

    @Test(expected = WsException.class)
    public void fileNameMaxLength__over_length_size_should_fail() {
        makeValidator("abcdefgh", 0).fileNameMaxLength(7);
    }
}
