package com.coreoz.plume.file.utils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileNames {

    private FileNames() {
        // empty constructor
    }

    /**
     * Remove all weird characters while trying to ensure
     * the sanitize filename is close to the original one:
     * - Accents are stripped, spaces are replaced by -,
     * - Upper case chars are converted to lower case.
     *
     * @param filename the file name
     * @return the clean file name, null if the filename is null
     */
    @Nullable
    public static String clean(String filename) {
        if (filename == null) {
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
     * @param filename the file name
     * @return the mime type guessed, null if no one is found
     */
    @Nullable
    public static String guessMimeType(String filename) {
        if (filename == null) {
            return null;
        }

        try {
            return Files.probeContentType(Paths.get(filename));
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean extensionNameHasAccents(String fileExtension) {
        return StringUtils.hasAccents(fileExtension);
    }

    @Nullable
    public static String parseFileNameExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            return null;
        }
        return fileName.substring(dotIndex + 1);
    }

    @Nullable
    public static String cleanExtensionName(String fileExtension) {
        if (fileExtension == null) {
            return null;
        }
        return fileExtension.toLowerCase().replace(".", "");
    }
}
