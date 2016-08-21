package com.coreoz.plume.file.services.cache;

import lombok.Value;

@Value(staticConstructor = "of")
public class CachedFile {

	private final String mimeType;
	private final String checksum;
	private final byte[] data;

}
