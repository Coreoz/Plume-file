package com.coreoz.plume.file.utils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileNameUtils {

	/**
	 * Remove all weird characters while trying to ensure
	 * the sanitize filename is close to the original one:
	 * accent are stripped, spaces are replaced by -,
	 * upper case chars are converted to lower case.
	 *
	 * If the filename is null, null is returned
	 */
	public static String sanitize(String filename) {
		if(filename == null) {
			return null;
		}

		return StringUtils
				.stripAccents(filename)
				.toLowerCase()
				.replaceAll("\\s+", "-")
				.replaceAll("[^a-z0-9-_\\.]", "");
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

	/**
	 * format file URL to have a properly formatted URL
	 *
	 * can be used outside the librarie to help users to format their custom plume-file url
	 */
	public static String formatUrl(String basePath, String wsPath, String fileUid, @Nullable String fileName) {
		String res = String.format(
			"/%s/%s/%s",
			trimSlash(basePath),
			trimSlash(wsPath),
			trimSlash(fileUid)
		);
		String trimmedFileName = trimSlash(fileName);
		if (trimmedFileName == null) {
			return res;
		}
		return res + "/" + trimmedFileName;
	}

	/**
	 * Removes beginning and trailing slashes of a string value to avoid duplicates
	 *
	 */
	private static String trimSlash(String value) {
		if (value == null) {
			return null;
		}
		if (value.charAt(0) == '/') {
			value = value.substring(1);
		}
		if (value.charAt(value.length() - 1)  == '/') {
			value = value.substring(0, value.length() - 2);
		}
		return value;
	}

}
