package com.coreoz.plume.file.validator;

import com.coreoz.plume.file.utils.FileNameCleaning;
import com.coreoz.plume.file.services.mimetype.FileMimeTypeDetector;
import com.coreoz.plume.file.services.mimetype.PeekingInputStream;
import com.coreoz.plume.file.utils.FileExtensionCleaning;
import com.coreoz.plume.jersey.errors.Validators;
import com.coreoz.plume.jersey.errors.WsError;
import com.coreoz.plume.jersey.errors.WsException;
import com.google.common.base.Strings;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Validate files data with a fluent API.<br>
 * <br>
 * Usage: the first method {@code FileUploadSizeValidator.from(uploadBodyPart)} must be called, then the proposed fluent methods
 * must be called one after another until the {@code finish()} call. For exemple:
 * <pre>
 * {@code
 * // The instance of fileMimeTypeDetector should be obtained by dependency injection from Plume file Core
 * FileUploadData fileUploadData = FileUploadValidator
 *   .from(fileMetadata, fileData, fileMimeTypeDetector)
 *   .fileMaxSize(2_000_000)
 *   .fileNameNotEmpty()
 *   .fileNameMaxDefaultLength()
 *   .fileExtensionNotEmpty()
 *   .fileExtension(Set.of("docx", "pdf"))
 *   .sanitizeFileName()
 *   .finish();
 * }
 * </pre>
 */
public class FileUploadValidator implements FileUploadSizeValidator, FileUploadEmptyNameValidator,
    FileUploadNameLengthValidator, FileUploadGeneralExtensionAndTypeValidator,
    FileUploadExtensionValidator, FileUploadTypeValidator, FileUploadFinisher, FileUploadDataBuilder {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadValidator.class);

    private FileUploadData data;

    private FileUploadValidator(
        String fileName,
        long fileSize,
        InputStream fileData,
        FileMimeTypeDetector fileMimeTypeDetector
    ) {
        try {
            PeekingInputStream filePeekingStream = new PeekingInputStream(fileData);
            String mimeType = fileMimeTypeDetector.guessMimeType(fileName, filePeekingStream);
            this.data = new FileUploadData(
                filePeekingStream.peekedStream(),
                fileName,
                FileExtensionCleaning.parseFileNameExtension(fileName),
                mimeType,
                fileSize
            );
        } catch (IOException e) {
            logger.warn("Could not extract mime type", e);
            throw new WsException(WsError.REQUEST_INVALID, "Could not read file data");
        }
    }

    /**
     * Starts a new validation process using Jersey fields FormDataBodyPart and InputStream.
     * See {@link #from(String, long, InputStream, FileMimeTypeDetector)} for streaming upload
     * not relying on Jersey.
     */
    public static FileUploadSizeValidator from(FormDataBodyPart fileMetadata, InputStream fileData,
                                               FileMimeTypeDetector fileMimeTypeDetector) {
        Validators.checkRequired(fileMetadata);
        return from(
            fileMetadata.getContentDisposition().getFileName(),
            fileMetadata.getFormDataContentDisposition().getSize(),
            fileData,
            fileMimeTypeDetector
        );
    }

    /**
     * Starts a new validation process using Jersey fields FormDataBodyPart and InputStream
     * @param fileName the file name
     * @param fileSize the file size in bytes
     *                 it's usually the value of the Header Content-Length,
     *                 considering that the rest of the MultiPart body is insignificant
     * @param fileData the file InputStream
     * @param fileMimeTypeDetector the mime type detector {@link FileMimeTypeDetector}
     * @return a {@link FileUploadSizeValidator} builder
     */
    public static FileUploadSizeValidator from(
        String fileName,
        long fileSize,
        InputStream fileData,
        FileMimeTypeDetector fileMimeTypeDetector
    ) {
        Validators.checkRequired(fileData);
        Objects.requireNonNull(fileMimeTypeDetector, "The instance of fileMimeTypeDetector should be obtained by dependency injection from Plume file Core");
        return new FileUploadValidator(fileName, fileSize, fileData, fileMimeTypeDetector);
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
     * Compares the file mime type with a given authorized mime types Set<br>
     * <br>
     * <strong>Warning: be careful with mime types verification, many alias exists, and it is difficult to get them all.
     * For example, for xml, you can get "text/xml" or "application/xml"</strong>
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

    /**
     * Remove all weird characters while trying to ensure the sanitize file name is close to the original one.
     * @see FileNameCleaning#cleanFileName(String)
     */
    public FileUploadDataBuilder sanitizeFileName() {
        return this.changeFileName(FileNameCleaning::cleanFileName);
    }

    /**
     * Customize how to change the file name.
     */
    public FileUploadDataBuilder changeFileName(UnaryOperator<String> fileNameMapper) {
        this.data = new FileUploadData(
            this.data.getFileData(),
            fileNameMapper.apply(this.data.getFileName()),
            this.data.getFileExtension(),
            this.data.getMimeType(),
            this.data.getFileSize()
        );
        return this;
    }

    /**
     * Keep the file name exactly how it was sent without any transformation.
     */
    @Override
    public FileUploadDataBuilder keepOriginalFileName() {
        return this;
    }

    @Override
    public FileUploadData finish() {
        return this.data;
    }
}
