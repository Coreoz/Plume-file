package com.coreoz.plume.file.utils;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class to sanitize and validate file paths for subdirectory support.
 * Ensures paths are safe and do not allow filesystem traversal attacks.
 */
public class FilePathSanitizer {

    private FilePathSanitizer() {
        // Utility class
    }

    /**
     * Sanitizes a file path to ensure it's safe for use.
     *
     * @param path The path to sanitize (can be null)
     * @return The sanitized path, or null if the input was null or empty
     * @throws IllegalArgumentException if the path contains invalid patterns like "..", is absolute, or attempts path traversal
     */
    public static String sanitizePath(@Nullable String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        // Trim whitespace
        String sanitized = path.trim();

        // Reject absolute paths BEFORE any manipulation (Windows and Unix)
        if (isAbsolutePathBeforeTrimming(path)) {
            throw new IllegalArgumentException("Absolute paths are not allowed: " + path);
        }

        // Convert backslashes to forward slashes for consistency
        sanitized = sanitized.replace('\\', '/');

        // Remove leading and trailing slashes
        while (sanitized.startsWith("/")) {
            sanitized = sanitized.substring(1);
        }
        while (sanitized.endsWith("/")) {
            sanitized = sanitized.substring(0, sanitized.length() - 1);
        }

        // After trimming, check if empty
        if (sanitized.isEmpty()) {
            return null;
        }

        // Reject paths containing ".."
        if (sanitized.contains("..")) {
            throw new IllegalArgumentException("Path traversal patterns (..) are not allowed: " + path);
        }

        // Check for empty segments (e.g., "foo//bar")
        String[] segments = sanitized.split("/");
        for (String segment : segments) {
            if (segment.isEmpty()) {
                throw new IllegalArgumentException("Path contains empty segments: " + path);
            }
        }

        // Validate the path doesn't escape using Path normalization
        try {
            Path normalizedPath = Paths.get(sanitized).normalize();
            if (normalizedPath.isAbsolute() || normalizedPath.toString().startsWith("..")) {
                throw new IllegalArgumentException("Path attempts to escape base directory: " + path);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid path: " + path, e);
        }

        return sanitized;
    }

    /**
     * Checks if a path is absolute BEFORE any trimming (Windows or Unix style)
     * This is used to detect absolute paths in the original input
     */
    private static boolean isAbsolutePathBeforeTrimming(@Nullable String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        String trimmed = path.trim();

        // Windows absolute path (e.g., C:\, C:/, \\server\share)
        if (trimmed.length() >= 2) {
            char firstChar = trimmed.charAt(0);
            char secondChar = trimmed.charAt(1);

            // Drive letter (C:)
            if (Character.isLetter(firstChar) && secondChar == ':') {
                return true;
            }

            // UNC path (\\)
            return trimmed.startsWith("\\\\");
        }

        // Unix absolute path should be rejected only if it's truly absolute,
        // not just a relative path with leading slash that we can trim
        // We'll allow leading slashes to be trimmed
        return false;
    }

    /**
     * Combines a base path with a subdirectory path safely.
     *
     * @param basePath The base directory path
     * @param subPath The subdirectory path (sanitized)
     * @return The combined path with proper separator
     */
    public static String combinePaths(String basePath, @Nullable String subPath) {
        if (subPath == null || subPath.isEmpty()) {
            return basePath;
        }

        String base = basePath;
        if (!base.endsWith("/") && !base.endsWith("\\")) {
            base += "/";
        }

        return base + subPath + "/";
    }
}

