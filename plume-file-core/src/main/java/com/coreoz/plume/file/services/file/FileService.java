package com.coreoz.plume.file.services.file;

import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

import javax.annotation.Nullable;

import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.file.data.FileUploadBase64;
import com.coreoz.plume.file.services.file.data.FileUploaded;
import com.coreoz.plume.file.services.filetype.FileType;
import com.google.common.io.ByteStreams;

import lombok.SneakyThrows;

public interface FileService {

	// upload

	/**
	 * Save an uploaded file.
	 */
	FileUploaded upload(FileType fileType, byte[] fileData, @Nullable String fileName);

	// upload alias
	default FileUploaded upload(FileType fileType, byte[] fileData) {
		return upload(fileType, fileData, null);
	}
	default Optional<FileUploaded> upload(FileType fileType, @Nullable FileUploadBase64 file) {
		if(file == null || file.getBase64() == null) {
			return Optional.empty();
		}

		return Optional.of(upload(fileType, Base64.getDecoder().decode(file.getBase64()), file.getFilename()));
	}
	/**
	 * Consume the stream to produce a byte array,
	 * then call {@link #upload(byte[], String, EntityManager))}
	 */
	default FileUploaded upload(FileType fileType, InputStream fileData) {
		return upload(fileType, fileData, null);
	}
	/**
	 * Consume the stream to produce a byte array,
	 * then call {@link #upload(byte[], String, EntityManager))}
	 */
	@SneakyThrows
	default FileUploaded upload(FileType fileType, InputStream fileData, String fileName) {
		return upload(fileType, ByteStreams.toByteArray(fileData), fileName);
	}

	// delete

	void delete(Long fileId);

	void deleteUnreferenced();

	// file URL

	/**
	 * Returns a complete relative URL, for example /api/files/3418557718705733633/cats.jpg
	 * {@link Optional#empty()} is returned if the corresponding file does not exist
	 */
	Optional<String> url(Long fileId);
	/**
	 * Returns a relative URL without the extension.
	 * Contrary to {@link #url(Long)},
	 * it does not make a call to the database to compute the file URL.
	 *
	 * For example, returns /api/file/3418557718705733633
	 */
	String urlRaw(Long fileId);

	// file data

	Optional<FileData> fetch(Long fileId);


}
