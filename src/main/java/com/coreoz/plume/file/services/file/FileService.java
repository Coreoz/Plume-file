package com.coreoz.plume.file.services.file;

import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;

import com.coreoz.plume.file.db.entities.FileEntity;
import com.google.common.io.ByteStreams;

import lombok.SneakyThrows;

public interface FileService {

	// upload

	/**
	 * Save an uploaded file.
	 */
	FileUploaded upload(byte[] fileData, @Nullable String fileName, @Nullable EntityManager transaction);

	// upload alias
	default FileUploaded upload(byte[] fileData) {
		return upload(fileData, null);
	}
	default FileUploaded upload(byte[] fileData, String fileName) {
		return upload(fileData, null, null);
	}
	default FileUploaded upload(FileUploadBase64 file) {
		return upload(file, null);
	}
	default FileUploaded upload(FileUploadBase64 file, EntityManager transaction) {
		return upload(Base64.getDecoder().decode(file.getBase64()), file.getFilename(), transaction);
	}
	/**
	 * Consume the stream to produce a byte array,
	 * then call {@link #upload(byte[], String, EntityManager))}
	 */
	default FileUploaded upload(InputStream fileData) {
		return upload(fileData, null, null);
	}
	/**
	 * Consume the stream to produce a byte array,
	 * then call {@link #upload(byte[], String, EntityManager))}
	 */
	default FileUploaded upload(InputStream fileData, String fileName) {
		return upload(fileData, fileName, null);
	}
	/**
	 * Consume the stream to produce a byte array,
	 * then call {@link #upload(byte[], String, EntityManager))}
	 */
	@SneakyThrows
	default FileUploaded upload(InputStream fileData, String fileName, EntityManager transaction) {
		return upload(ByteStreams.toByteArray(fileData), fileName, transaction);
	}

	// delete

	default void delete(Long fileId) {
		delete(fileId, null);
	}
	void delete(Long fileId, EntityManager transaction);

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
	/**
	 * Returns a complete relative URL for all ids passed as parameter.
	 * It will only make one request to the database to fetch the filenames for all ids.
	 * The URL returned are like the ones provided by {@link #url(Long)}.
	 */
	List<FileUploaded> urlBatch(List<Long> fileIds);

	// file data

	Optional<FileEntity> fetch(Long fileId);


}
