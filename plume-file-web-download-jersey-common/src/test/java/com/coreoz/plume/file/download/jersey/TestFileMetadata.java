package com.coreoz.plume.file.download.jersey;

import com.coreoz.plume.file.services.metadata.FileMetadata;

import java.time.Instant;

/**
 * Simple test double for {@link FileMetadata}.
 */
public class TestFileMetadata implements FileMetadata {
	private final String uniqueName;
	private final String originalName;
	private final String fileExtension;
	private final String mimeType;
	private final String checksum;

	public TestFileMetadata(
		String uniqueName,
		String originalName,
		String fileExtension,
		String mimeType,
		String checksum
	) {
		this.uniqueName = uniqueName;
		this.originalName = originalName;
		this.fileExtension = fileExtension;
		this.mimeType = mimeType;
		this.checksum = checksum;
	}

	@Override
	public String getUniqueName() { return uniqueName; }
	@Override
	public String getFileOriginalName() { return originalName; }
	@Override
	public String getFileType() { return "TEST"; }
	@Override
	public String getFileExtension() { return fileExtension; }
	@Override
	public String getMimeType() { return mimeType; }
	@Override
	public String getChecksum() { return checksum; }
	@Override
	public Long getFileSize() { return 11L; }
	@Override
	public Instant getCreationDate() { return Instant.now(); }
}
