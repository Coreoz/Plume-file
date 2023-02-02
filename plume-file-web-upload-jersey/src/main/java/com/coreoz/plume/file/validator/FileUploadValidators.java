package com.coreoz.plume.file.validator;

import com.coreoz.plume.file.data.FileUploadMetadata;
import com.coreoz.plume.file.utils.FileNameUtils;
import com.coreoz.plume.jersey.errors.WsError;
import com.coreoz.plume.jersey.errors.WsException;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.util.Objects;
import java.util.Set;

public class FileUploadValidators {

    private FileUploadValidators() {
        // empty constructor to hide the implicit one
    }

    public static void verifyRequiredMetadata(FileUploadMetadata fileMetadata) {
        Objects.requireNonNull(fileMetadata);
        Objects.requireNonNull(fileMetadata.getMimeType());
    }

    public static void verifyFileNameLength(FormDataBodyPart formDataBodyPart, long fileNameMaxSize) {
        String fileName = formDataBodyPart.getContentDisposition().getFileName();
        Objects.requireNonNull(fileName);
        if (fileName.length() > fileNameMaxSize) {
            throw new WsException(WsError.REQUEST_INVALID, "Filename too long");
        }
    }

    public static void verifyFileSize(FormDataBodyPart formDataBodyPart, long fileMaxSizeInBytes) {
        // 1 000 000 bytes = 1 000 000 octets = 1MB ~= 8Mb ~= 0,95Mio
        long fileSize = formDataBodyPart.getFormDataContentDisposition().getSize();
        if (fileSize > fileMaxSizeInBytes) {
            throw new WsException(WsError.REQUEST_INVALID, "File too large");
        }
    }

    public static void verifyFileExtension(FormDataBodyPart formDataBodyPart, Set<String> authorizedExtension) {
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

    public static void verifyFileMediaType(FormDataBodyPart formDataBodyPart, Set<String> authorizedMimeTypes) {
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
