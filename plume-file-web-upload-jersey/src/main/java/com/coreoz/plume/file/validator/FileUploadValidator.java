package com.coreoz.plume.file.validator;

import com.coreoz.plume.file.utils.FileNames;
import com.coreoz.plume.jersey.errors.Validators;
import com.coreoz.plume.jersey.errors.WsError;
import com.coreoz.plume.jersey.errors.WsException;
import com.google.common.base.Strings;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.util.Set;

/**
 * Validate files data with a fluent API.<br>
 * <br>
 * Usage: the first method {@code FileUploadSizeValidator.from(uploadBodyPart)} must be called, then the proposed fluent methods
 * must be called one after another until the {@code finish()} call. For exemple:
 * <pre>
 * {@code
 * FileUploadData fileUploadMetadata = FileUploadValidator
 *   .from(fileMetadata)
 *   .fileMaxSize(2_000_000)
 *   .fileNameNotEmpty()
 *   .fileNameMaxDefaultLength()
 *   .fileExtensionNotEmpty()
 *   .fileExtension(Set.of("docx", "pdf"))
 *   .finish();
 * }
 * </pre>
 */
public class FileUploadValidator implements FileUploadSizeValidator, FileUploadEmptyNameValidator,
    FileUploadNameLengthValidator, FileUploadGeneralExtensionAndTypeValidator,
    FileUploadExtensionValidator, FileUploadTypeValidator, FileUploadFinisher {

    private final FileUploadMetadata data;

    private FileUploadValidator(FormDataBodyPart fileMetadata) {
        String fileName = fileMetadata.getContentDisposition().getFileName();
        this.data = new FileUploadMetadata(
            fileName,
            FileNames.parseFileNameExtension(fileName),
            FileNames.guessMimeType(fileName),
            fileMetadata.getFormDataContentDisposition().getSize()
        );
    }

    /**
     * Starts a new validation process using Jersey FormDataBodyPart
     */
    public static FileUploadSizeValidator from(FormDataBodyPart fileMetadata) {
        Validators.checkRequired(fileMetadata);
        return new FileUploadValidator(fileMetadata);
    }

    /**
     * Verify the file size in bytes with a given maximum size
     * @implNote 1 000 000 bytes = 1 000 000 octets = 1MB ~= 8Mb ~= 0,95Mio
     * @param fileMaxSizeInBytes the maximum file length in bytes authorized
     * @throws WsException if the file size in bytes is bigger than the maximum authorized
     */
    public FileUploadValidator fileMaxSize(long fileMaxSizeInBytes) {
        if (data.getFileSize() > fileMaxSizeInBytes) {
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
        if (Strings.isNullOrEmpty(data.getFileName())) {
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
        if (data.getFileName() != null && data.getFileName().length() > fileNameMaxLength) {
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
        if (Strings.isNullOrEmpty(data.getFileExtension())) {
            throw new WsException(WsError.REQUEST_INVALID, "File extension cannot be empty");
        }
        return this;
    }

    public FileUploadValidator fileTypeNotEmpty() {
        if (Strings.isNullOrEmpty(data.getMimeType())) {
            throw new WsException(WsError.REQUEST_INVALID, "Unrecognized mime type");
        }
        return this;
    }

    /**
     * Verify that the file extension length (with the first .) is not longer than the limit. The default limit is 10.
     * @param fileExtensionMaxLength the maximum file extension length authorized
     * @throws WsException if the file extension length is longer than the limit
     */
    public FileUploadValidator fileExtensionMaxLength(long fileExtensionMaxLength) {
        if (data.getFileExtension() != null && data.getFileExtension().length() > fileExtensionMaxLength) {
            throw new WsException(WsError.REQUEST_INVALID, "File extension is too long");
        }
        return this;
    }

    /**
     * Verify that the file extension length (with the first .) is not longer than 10 (the default limit)
     * @throws WsException if the file extension length is longer than the limit
     */
    @Override
    public FileUploadValidator fileExtensionMaxDefaultLength() {
        return fileExtensionMaxLength(10);
    }

    /**
     * Compares the file extension with a given authorized extensions Set
     * @param authorizedExtension the authorized extensions
     * @throws WsException if the file extension is not in the authorized extensions
     */
    public FileUploadValidator fileExtensions(Set<String> authorizedExtension) {
        if (data.getFileExtension() != null && !authorizedExtension.contains(data.getFileExtension())) {
            throw new WsException(WsError.REQUEST_INVALID, "File extension is not supported");
        }
        return this;
    }

    /**
     * Compares the file mime type with a given authorized mime types Set
     * @param authorizedMimeTypes the authorized mime types
     * @throws WsException if the file mime type is not in the authorized mime types
     */
    public FileUploadValidator mimeTypes(Set<String> authorizedMimeTypes) {
        if (data.getMimeType() != null && !authorizedMimeTypes.contains(data.getMimeType())) {
            throw new WsException(WsError.REQUEST_INVALID, "File mime type not supported");
        }
        return this;
    }

    /**
     * Verify that a file is an image
     * @throws WsException if the file mime type is not recognized as an image
     */
    public FileUploadValidator fileImage() {
        fileTypeNotEmpty();
        if (!data.getMimeType().startsWith("image/")) {
            throw new WsException(WsError.REQUEST_INVALID, "File type is not an image");
        }
        return this;
    }

    @Override
    public FileUploadMetadata finish() {
        return data;
    }
}
