package com.coreoz.plume.file.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilePathSanitizerTest {

    @Test
    void sanitizePath_should_return_null_for_null_input() {
        assertThat(FilePathSanitizer.sanitizePath(null)).isNull();
    }

    @Test
    void sanitizePath_should_return_null_for_empty_input() {
        assertThat(FilePathSanitizer.sanitizePath("")).isNull();
        assertThat(FilePathSanitizer.sanitizePath("   ")).isNull();
    }

    @Test
    void sanitizePath_should_remove_leading_slashes() {
        assertThat(FilePathSanitizer.sanitizePath("/test/path")).isEqualTo("test/path");
        assertThat(FilePathSanitizer.sanitizePath("///test/path")).isEqualTo("test/path");
    }

    @Test
    void sanitizePath_should_remove_trailing_slashes() {
        assertThat(FilePathSanitizer.sanitizePath("test/path/")).isEqualTo("test/path");
        assertThat(FilePathSanitizer.sanitizePath("test/path///")).isEqualTo("test/path");
    }

    @Test
    void sanitizePath_should_convert_backslashes_to_forward_slashes() {
        assertThat(FilePathSanitizer.sanitizePath("test\\path")).isEqualTo("test/path");
        assertThat(FilePathSanitizer.sanitizePath("test\\path\\subdir")).isEqualTo("test/path/subdir");
    }

    @Test
    void sanitizePath_should_reject_path_traversal() {
        assertThatThrownBy(() -> FilePathSanitizer.sanitizePath("../etc/passwd"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Path traversal patterns (..) are not allowed");

        assertThatThrownBy(() -> FilePathSanitizer.sanitizePath("test/../etc/passwd"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Path traversal patterns (..) are not allowed");
    }


    @Test
    void sanitizePath_should_reject_absolute_windows_paths() {
        assertThatThrownBy(() -> FilePathSanitizer.sanitizePath("C:\\Windows\\System32"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Absolute paths are not allowed");

        assertThatThrownBy(() -> FilePathSanitizer.sanitizePath("D:/data/files"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Absolute paths are not allowed");
    }

    @Test
    void sanitizePath_should_reject_unc_paths() {
        assertThatThrownBy(() -> FilePathSanitizer.sanitizePath("\\\\server\\share"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Absolute paths are not allowed");
    }

    @Test
    void sanitizePath_should_reject_empty_segments() {
        assertThatThrownBy(() -> FilePathSanitizer.sanitizePath("test//path"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Path contains empty segments");
    }

    @Test
    void sanitizePath_should_accept_valid_relative_paths() {
        assertThat(FilePathSanitizer.sanitizePath("documents")).isEqualTo("documents");
        assertThat(FilePathSanitizer.sanitizePath("images/2024")).isEqualTo("images/2024");
        assertThat(FilePathSanitizer.sanitizePath("files/project/assets")).isEqualTo("files/project/assets");
    }

    @Test
    void combinePaths_should_combine_base_and_subpath() {
        assertThat(FilePathSanitizer.combinePaths("/base/path", "sub/dir"))
            .isEqualTo("/base/path/sub/dir/");

        assertThat(FilePathSanitizer.combinePaths("/base/path/", "sub/dir"))
            .isEqualTo("/base/path/sub/dir/");
    }

    @Test
    void combinePaths_should_return_base_when_subpath_is_null() {
        assertThat(FilePathSanitizer.combinePaths("/base/path", null))
            .isEqualTo("/base/path");

        assertThat(FilePathSanitizer.combinePaths("/base/path", ""))
            .isEqualTo("/base/path");
    }
}

