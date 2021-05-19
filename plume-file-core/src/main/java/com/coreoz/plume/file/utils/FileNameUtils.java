package com.coreoz.plume.file.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileNameUtils {

	private FileNameUtils() {
		// empty constructor
	}

	/**
	 * Find the corresponding mime type for the file name.
	 *
	 * If no mime type if found or the file name is empty,
	 * null is returned.
	 */
	public static String guessMimeType(String filename) {
		if(filename == null) {
			return null;
		}

		try {
			return Files.probeContentType(Paths.get(filename));
		} catch (IOException e) {
			return null;
		}
	}

	public static String getExtensionFromFilename(String fileName) {
		if (fileName == null) {
			return "";
		}
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	public static String cleanExtensionName(String fileExtension) {
		return fileExtension.toLowerCase().replaceAll("[a-z0-9]", "");
	}
}
