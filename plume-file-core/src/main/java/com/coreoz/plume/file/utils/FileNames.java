package com.coreoz.plume.file.utils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class FileNames {

    private static final Pattern fileExtensionExcludePattern = Pattern.compile("[^a-zA-Z0-9]");
    private static final Pattern fileNameExcludePattern = Pattern.compile("[^a-zA-Z0-9-_\\.]");
    private static final Pattern spacesPattern = Pattern.compile("\\s+");

    private FileNames() {
        // empty constructor
    }

    /**
     * Remove all weird characters while trying to ensure
     * the sanitize file name is close to the original one:
     * - Accents are stripped, spaces are replaced by -,
     * - Upper case chars are converted to lower case.
     * - Other characters are removed
     *
     * @param fileName the file name, e.g. <code>dog.jpg</code>
     * @return the clean file name, null if the filename is null
     */
    @Nullable
    public static String cleanFileName(String fileName) {
        if (fileName == null) {
            return null;
        }

        return fileNameExcludePattern.matcher(
            spacesPattern.matcher(
                StringUtils.stripAccents(fileName)
            ).replaceAll("-")
        ).replaceAll("");
    }

    /**
     * Transform to extension to lower case and
     * remove all characters that are not numbers or between 'a' and 'z'
     *
     * @param fileExtension the file name extension, e.g. <code>jpg</code>
     * @return the clean file extension, null if fileExtension is null
     */
    @Nullable
    public static String cleanExtensionName(String fileExtension) {
        if (fileExtension == null) {
            return null;
        }
        return fileExtensionExcludePattern.matcher(fileExtension).replaceAll("").toLowerCase();
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
}
