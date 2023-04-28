package com.coreoz.plume.file.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// TODO à renommer en FileNames ?
public class FileNameUtils {

	private FileNameUtils() {
		// empty constructor
	}

	// TODO fournir une méthode pour nettoyer le nom du fichier non ?

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

	// TODO à renommer en parseFileNameExtension
	// TODO pourquoi ne pas renvoyer null s'il n'y a pas d'extension ? Je me dis que sémantiquement ça serait plus clair
	public static String getExtensionFromFilename(String fileName) {
		if (fileName == null) {
			return "";
		}
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex == -1) {
			return "";
		}
		return fileName.substring(dotIndex + 1);
	}

	public static String cleanExtensionName(String fileExtension) {
		if (fileExtension == null) {
			return "";
		}
		return fileExtension.toLowerCase().replace(".", "");
	}
}
