package com.coreoz.plume.file.validator;

import com.coreoz.plume.file.data.FileUploadMetadata;
import com.coreoz.plume.file.utils.FileNameUtils;
import com.coreoz.plume.jersey.errors.WsError;
import com.coreoz.plume.jersey.errors.WsException;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.util.List;
import java.util.Objects;

public class FileUploadValidators {

    private FileUploadValidators() {
        // empty constructor to hide the implicit one
    }

    public static void verifyRequiredMetadata(FileUploadMetadata fileMetadata) {
        Objects.requireNonNull(fileMetadata);
        Objects.requireNonNull(fileMetadata.getMimeType());
    }

    public static void verifyFileNameLength(FormDataBodyPart formDataBodyPart, long fileMaxSize) {
        String fileName = formDataBodyPart.getContentDisposition().getFileName();
        Objects.requireNonNull(fileName);
        if (fileName.length() > fileMaxSize) {
            throw new WsException(WsError.REQUEST_INVALID, "Filename too long");
        }
    }

    public static void verifyFileSize(FormDataBodyPart formDataBodyPart, long fileMaxSize) {
        long fileSize = formDataBodyPart.getFormDataContentDisposition().getSize();
        if (fileSize > fileMaxSize) {
            throw new WsException(WsError.REQUEST_INVALID, "File too large");
        }
    }

    public static void verifyFileExtension(FormDataBodyPart formDataBodyPart, List<String> authorizedExtension) {
        String fileExtension = FileNameUtils.getExtensionFromFilename(
            formDataBodyPart.getContentDisposition().getFileName()
        );
        if (StringUtils.isEmpty(fileExtension)) {
            throw new NullPointerException();
        }
        if (!authorizedExtension.contains(fileExtension)) {
            throw new WsException(WsError.REQUEST_INVALID, "File extension not supported");
        }
    }

    public static void verifyFileMediaType(FormDataBodyPart formDataBodyPart, List<String> authorizedMimeTypes) {
        String fileMimeType = FileNameUtils.getExtensionFromFilename(
            formDataBodyPart.getMediaType().toString()
        );
        if (StringUtils.isEmpty(fileMimeType)) {
            throw new NullPointerException();
        }
        if (!authorizedMimeTypes.contains(fileMimeType)) {
            throw new WsException(WsError.REQUEST_INVALID, "File mime type not supported");
        }
    }
}
