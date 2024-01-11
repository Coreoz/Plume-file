package com.coreoz.plume.file.utils;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * For removal in V4
 * Call {@link com.coreoz.plume.file.cleaning.FileExtensionCleaning} instead
 */
@Deprecated(forRemoval = true)
public class FileNames {
    private static final Pattern fileExtensionExcludePattern = Pattern.compile("[^a-zA-Z0-9]");

    private FileNames() {
        // empty constructor
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
