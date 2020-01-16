package com.coreoz.plume.file.services.file;

import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.file.data.FileUploadBase64;
import com.coreoz.plume.file.services.file.data.FileUploaded;
import com.coreoz.plume.file.services.filetype.FileType;
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
	 * then call {@link #upload(FileType, InputStream, String))}
	 */
	default FileUploaded upload(FileType fileType, InputStream fileData) {
		return upload(fileType, fileData, null);
	}
	/**
	 * Consume the stream to produce a byte array,
	 * then call {@link #upload(FileType, byte[], String))}
	 */
	@SneakyThrows
	default FileUploaded upload(FileType fileType, InputStream fileData, String fileName) {
		return upload(fileType, ByteStreams.toByteArray(fileData), fileName);
	}

	// delete

	void delete(String fileUid);

	void deleteUnreferenced();

	// file URL

	/**
	 * Returns a complete relative URL, for example /api/files/3418557718705733633/cats.jpg
	 * {@link Optional#empty()} is returned if the corresponding file does not exist
	 */
	Optional<String> url(String fileUid);

	/**
	 * Returns a relative URL without the extension.
	 * Contrary to {@link #url(String)},
	 * it does not make a call to the database to compute the file URL.
	 *
	 * For example, returns /api/file/3418557718705733633
	 */
	String urlRaw(String fileUid);

	// file data

	Optional<FileData> fetch(String fileUid);


}
