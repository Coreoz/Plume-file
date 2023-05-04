package com.coreoz.plume.file.validator;

import com.coreoz.plume.file.utils.FileNames;
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

    /**
     * Compares the file name length (with the extension part) with a given maximum length
     * @param formDataBodyPart the form data received from Jersey
     * @param fileNameMaxSize the maximum file name length authorized
     * @throws WsException if the file name length is bigger than the maximum authorized
     */
    public static void verifyFileNameLength(FormDataBodyPart formDataBodyPart, long fileNameMaxSize) {
        String fileName = formDataBodyPart.getContentDisposition().getFileName();
        Objects.requireNonNull(fileName);
        if (fileName.length() > fileNameMaxSize) {
            throw new WsException(WsError.REQUEST_INVALID, "Filename too long");
        }
    }

    /**
     * Compares the file size in bytes with a given maximum size
     * @implNote 1 000 000 bytes = 1 000 000 octets = 1MB ~= 8Mb ~= 0,95Mio
     * @param formDataBodyPart the form data received from Jersey
     * @param fileMaxSizeInBytes the maximum file length in bytes authorized
     * @throws WsException if the file size in bytes is bigger than the maximum authorized
     */
    public static void verifyFileSize(FormDataBodyPart formDataBodyPart, long fileMaxSizeInBytes) {
        // 1 000 000 bytes = 1 000 000 octets = 1MB ~= 8Mb ~= 0,95Mio
        long fileSize = formDataBodyPart.getFormDataContentDisposition().getSize();
        if (fileSize > fileMaxSizeInBytes) {
            throw new WsException(WsError.REQUEST_INVALID, "File too large");
        }
    }

    /**
     * Compares the file extension with a given authorized extensions Set
     * @param formDataBodyPart the form data received from Jersey
     * @param authorizedExtension the authorized extensions
     * @throws WsException if the file extension is not in the authorized extensions
     */
    public static void verifyFileExtension(FormDataBodyPart formDataBodyPart, Set<String> authorizedExtension) {
        String fileExtension = FileNames.parseFileNameExtension(
            formDataBodyPart.getContentDisposition().getFileName()
        );
        if (StringUtils.isEmpty(fileExtension)) {
            throw new WsException(WsError.REQUEST_INVALID, "Empty file extension not supported");
        }
        if (!authorizedExtension.contains(fileExtension)) {
            throw new WsException(WsError.REQUEST_INVALID, "File extension not supported");
        }
    }

    /**
     * Checks if the file extension :
     * - Contains accents
     * - Is longer than a given length
     * @param formDataBodyPart the form data received from Jersey
     * @param maxExtensionLength the max extension length auhtorized
     */
    public static void verifyFileExtensionFormat(FormDataBodyPart formDataBodyPart, Integer maxExtensionLength) {
        String fileExtension = FileNames.parseFileNameExtension(
            formDataBodyPart.getContentDisposition().getFileName()
        );
        if (StringUtils.isEmpty(fileExtension)) {
            throw new WsException(WsError.REQUEST_INVALID, "Empty file extension not supported");
        }
        if (fileExtension.length() > maxExtensionLength) {
            throw new WsException(WsError.REQUEST_INVALID, "File extension too long");
        }
        if (FileNames.extensionNameHasAccents(fileExtension)) {
            throw new WsException(WsError.REQUEST_INVALID, "Accents and diacritics in file extension are not supported");
        }
    }

    /**
     * Compares the file mime type with a given authorized mime types Set
     * @param formDataBodyPart the form data received from Jersey
     * @param authorizedMimeTypes the authorized mime types
     * @throws WsException if the file mime type is not in the authorized mime types
     */
    public static void verifyFileMediaType(FormDataBodyPart formDataBodyPart, Set<String> authorizedMimeTypes) {
        String fileMimeType = formDataBodyPart.getMediaType().toString();
        if (StringUtils.isEmpty(fileMimeType)) {
            throw new WsException(WsError.REQUEST_INVALID, "Unrecognized mime type");
        }
        if (!authorizedMimeTypes.contains(fileMimeType)) {
            throw new WsException(WsError.REQUEST_INVALID, "File mime type not supported");
        }
    }

    /**
     * Compares the file mime type with image available formats
     * @param formDataBodyPart the form data received from Jersey
     * @throws WsException if the file mime type is not in image available formats
     */
    public static void verifyFileImage(FormDataBodyPart formDataBodyPart) {
        String fileMimeType = formDataBodyPart.getMediaType().toString();
        if (StringUtils.isEmpty(fileMimeType)) {
            throw new WsException(WsError.REQUEST_INVALID, "Unrecognized mime type");
        }
        if (!fileMimeType.startsWith("image/")) {
            throw new WsException(WsError.REQUEST_INVALID, "Mime type not supported");
        }
    }
}
