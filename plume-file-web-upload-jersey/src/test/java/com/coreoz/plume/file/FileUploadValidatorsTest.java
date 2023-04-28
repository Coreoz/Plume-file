package com.coreoz.plume.file;

import com.coreoz.plume.file.validator.FileUploadValidators;
import com.coreoz.plume.jersey.errors.WsException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.Set;

public class FileUploadValidatorsTest {

    private FormDataBodyPart xmlBodyPart;
    private FormDataBodyPart untypedBodyPart;

    @Before
    public void createBodyParts() {
        FormDataContentDisposition xmlFormData = FormDataContentDisposition.name("test")
            .fileName("File Name.xml")
            .size(1000)
            .build();

        this.xmlBodyPart = new FormDataBodyPart(MediaType.TEXT_XML_TYPE);
        this.xmlBodyPart.setFormDataContentDisposition(xmlFormData);

        FormDataContentDisposition untypedFormData = FormDataContentDisposition.name("testUntyped")
            .fileName("File Name")
            .size(1000)
            .build();

        this.untypedBodyPart = new FormDataBodyPart();
        this.untypedBodyPart.setFormDataContentDisposition(untypedFormData);
    }

    @Test
    public void validate_accepted_extension_should_not_fail() {
        FileUploadValidators.verifyFileExtension(this.xmlBodyPart, Set.of("xml"));
    }

    @Test(expected = WsException.class)
    public void validate_not_accepted_extension_should_fail() {
        FileUploadValidators.verifyFileExtension(this.xmlBodyPart, Set.of("xlsx"));
    }

    @Test(expected = WsException.class)
    public void validate_untyped_extension_should_fail() {
        FileUploadValidators.verifyFileExtension(this.untypedBodyPart, Set.of("xlsx"));
    }

    @Test(expected = WsException.class)
    public void validate_xml_with_image_should_fail() {
        FileUploadValidators.verifyFileImage(this.xmlBodyPart);
    }

    @Test(expected = WsException.class)
    public void validate_untyped_with_image_should_fail() {
        FileUploadValidators.verifyFileImage(this.untypedBodyPart);
    }

    @Test
    public void validate_accepted_mime_type_should_not_fail() {
        FileUploadValidators.verifyFileMediaType(this.xmlBodyPart, Set.of("text/xml"));
    }

    @Test(expected = WsException.class)
    public void validate_not_accepted_mime_type_should_fail() {
        FileUploadValidators.verifyFileMediaType(this.xmlBodyPart, Set.of("application/json"));
    }

    @Test
    public void validate_file_max_size_with_correct_size_should_not_fail() {
        FileUploadValidators.verifyFileSize(this.xmlBodyPart, 1200);
    }

    @Test(expected = WsException.class)
    public void validate_file_max_size_with_wrong_size_should_fail() {
        FileUploadValidators.verifyFileSize(this.xmlBodyPart, 500);
    }

    @Test
    public void validate_file_name_max_size_with_correct_size_should_not_fail() {
        FileUploadValidators.verifyFileNameLength(this.xmlBodyPart, 13);
    }

    @Test(expected = WsException.class)
    public void validate_file_name_max_size_with_wrong_size_should_fail() {
        FileUploadValidators.verifyFileNameLength(this.xmlBodyPart, 8);
    }
}
