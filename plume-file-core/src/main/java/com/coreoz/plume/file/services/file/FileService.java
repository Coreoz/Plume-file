package com.coreoz.plume.file.services.file;

import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.file.data.FileUploadBase64;
import com.coreoz.plume.file.services.file.data.FileUploaded;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.utils.FileNameUtils;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

public interface FileService {

	// upload

	/**
	 * Save an uploaded file.
	 */
	FileUploaded upload(FileType fileType, String fileExtension, byte[] fileData);

	// upload alias
	default FileUploaded upload(FileType fileType, byte[] fileData) {
		return upload(fileType, null, fileData);
	}

	default Optional<FileUploaded> upload(FileType fileType, @Nullable FileUploadBase64 file) {
		if(file == null || file.getBase64() == null) {
			return Optional.empty();
		}

		return Optional.of(upload(fileType, FileNameUtils.getExtensionFromFilename(file.getFilename()), Base64.getDecoder().decode(file.getBase64())));
	}
	/**
	 * Consume the stream to produce a byte array,
	 * then call {@link #upload(FileType, InputStream, String))}
	 */
	default FileUploaded upload(FileType fileType, InputStream fileData) {
		return upload(fileType, fileData, null);
	}
	/**
	 * Consume the stream to produce a byte array,
	 * then call {@link #upload(FileType, String, byte[]))}
	 */
	@SneakyThrows
	default FileUploaded upload(FileType fileType, InputStream fileData, String fileName) {
		return upload(fileType, FileNameUtils.getExtensionFromFilename(fileName), ByteStreams.toByteArray(fileData));
	}

	// delete

	void delete(String fileUid);

	void delete(Long fileId);

	void deleteUnreferenced();

	// file URL

	/**
	 * Returns a complete relative URL, for example /api/files/846c36cc-f973-11e8-8eb2-f2801f1b9fd1/cats.jpg
	 * {@link Optional#empty()} is returned if the corresponding file does not exist
	 */
	Optional<String> url(String fileUid);

	Optional<String> url(Long fileId);

	/**
	 * Returns a relative URL without the extension.
	 * Contrary to {@link #url(String)},
	 * it does not make a call to the database to compute the file URL.
	 *
	 * For example, returns /api/file/846c36cc-f973-11e8-8eb2-f2801f1b9fd1
	 */
	String urlRaw(String fileUid);

	// file data

	Optional<FileData> fetch(String fileUid);

	Optional<FileData> fetch(Long fileId);


}
