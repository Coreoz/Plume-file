package com.coreoz.plume.file.services.file.data;

import lombok.Value;

@Value
public class FileData {
	String fileUniqueName;
	String mimeType;
	String checksum;
	byte[] data;
}
