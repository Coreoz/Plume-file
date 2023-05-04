package com.coreoz.plume.file.validator;

import com.coreoz.plume.file.utils.FileNames;
import com.coreoz.plume.jersey.errors.Validators;
import com.coreoz.plume.jersey.errors.WsError;
import com.coreoz.plume.jersey.errors.WsException;
import com.google.common.base.Strings;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.util.Set;

/**
 * Validate files data with a fluent API
 */
public class FileUploadValidator implements FileUploadSizeValidator, FileUploadEmptyNameValidator,
    FileUploadNameLengthValidator, FileUploadGeneralExtensionAndTypeValidator,
    FileUploadExtensionValidator, FileUploadTypeValidator {
    private final String fileName;
    private final String fileExtension;
    private final String mediaType;
    private final long fileSize;

    private FileUploadValidator(FormDataBodyPart formDataBodyPart) {
        this.fileName = formDataBodyPart.getContentDisposition().getFileName();
        this.fileExtension = FileNames.parseFileNameExtension(this.fileName);
        this.mediaType = FileNames.guessMimeType(this.fileName);
        this.fileSize = formDataBodyPart.getFormDataContentDisposition().getSize();
    }

    /**
     * Starts a new validation process using Jersey FormDataBodyPart
     */
    public static FileUploadSizeValidator from(FormDataBodyPart formDataBodyPart) {
        Validators.checkRequired(formDataBodyPart);
        return new FileUploadValidator(formDataBodyPart);
    }

    /**
     * Verify the file size in bytes with a given maximum size
     * @implNote 1 000 000 bytes = 1 000 000 octets = 1MB ~= 8Mb ~= 0,95Mio
     * @param fileMaxSizeInBytes the maximum file length in bytes authorized
     * @throws WsException if the file size in bytes is bigger than the maximum authorized
     */
    public FileUploadValidator fileMaxSize(long fileMaxSizeInBytes) {
        if (fileSize > fileMaxSizeInBytes) {
            throw new WsException(WsError.REQUEST_INVALID, "File length is too big");
        }
        return this;
    }

    /**
     * Verify that the file name is not empty:<ul>
     *     <li>"test.txt" -> pass</li>
     *     <li>"" -> fails</li>
     * </ul>
     */
    public FileUploadValidator fileNameNotEmpty() {
        if (Strings.isNullOrEmpty(fileName)) {
            throw new WsException(WsError.REQUEST_INVALID, "File name cannot be empty");
        }
        return this;
    }

    /**
     * Verify that the file name length (with the extension part) is not longer than the limit (255 is the default)
     * @param fileNameMaxLength the maximum file name length authorized
     * @throws WsException if the file name length is longer than the limit
     */
    public FileUploadValidator fileNameMaxLength(long fileNameMaxLength) {
        if (fileName != null && fileName.length() > fileNameMaxLength) {
            throw new WsException(WsError.REQUEST_INVALID, "File name is too long");
        }
        return this;
    }

    /**
     * Verify that the file name length (with the extension part) is not longer than the default limit, i.e. 255 characters
     * @throws WsException if the file extension length is longer than the 255 characters limit
     */
    @Override
    public FileUploadGeneralExtensionAndTypeValidator fileNameMaxDefaultLength() {
        return fileNameMaxLength(255);
    }

    public FileUploadValidator fileExtensionNotEmpty() {
        if (Strings.isNullOrEmpty(fileExtension)) {
            throw new WsException(WsError.REQUEST_INVALID, "File extension cannot be empty");
        }
        return this;
    }

    public FileUploadValidator fileTypeNotEmpty() {
        if (Strings.isNullOrEmpty(mediaType)) {
            throw new WsException(WsError.REQUEST_INVALID, "Unrecognized mime type");
        }
        return this;
    }

    /**
     * Verify that the file extension length (with the first .) is not longer than the limit. The default limit is 10.
     * @param fileExtensionMaxLength the maximum file extension length authorized
     * @throws WsException if the file extension length is longer than the limit
     */
    public void fileExtensionMaxLength(long fileExtensionMaxLength) {
        if (fileExtension != null && fileExtension.length() > fileExtensionMaxLength) {
            throw new WsException(WsError.REQUEST_INVALID, "File extension is too long");
        }
    }

    /**
     * Verify that the file extension length (with the first .) is not longer than 10 (the default limit)
     * @throws WsException if the file extension length is longer than the limit
     */
    @Override
    public void fileExtensionMaxDefaultLength() {
        fileExtensionMaxLength(10);
    }

    /**
     * Compares the file extension with a given authorized extensions Set
     * @param authorizedExtension the authorized extensions
     * @throws WsException if the file extension is not in the authorized extensions
     */
    public void fileExtensions(Set<String> authorizedExtension) {
        if (fileExtension != null && !authorizedExtension.contains(fileExtension)) {
            throw new WsException(WsError.REQUEST_INVALID, "File extension is not supported");
        }
    }

    /**
     * Compares the file mime type with a given authorized mime types Set
     * @param authorizedMimeTypes the authorized mime types
     * @throws WsException if the file mime type is not in the authorized mime types
     */
    public void mediaTypes(Set<String> authorizedMimeTypes) {
        if (mediaType != null && !authorizedMimeTypes.contains(mediaType)) {
            throw new WsException(WsError.REQUEST_INVALID, "File mime type not supported");
        }
    }

    /**
     * Verify that a file is an image
     * @throws WsException if the file mime type is not recognized as an image
     */
    public void fileImage() {
        fileTypeNotEmpty();
        if (!mediaType.startsWith("image/")) {
            throw new WsException(WsError.REQUEST_INVALID, "File type is not an image");
        }
    }
}
