package com.coreoz.plume.file.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FileNameCleaningTest {
    @Test
    public void cleanFileName__should_return_accents_string_without_accents() {
        assertThat(FileNameCleaning.cleanFileName("éçàyt.jpg"))
            .isEqualTo("ecayt.jpg");
    }

    @Test
    public void cleanFileName__should_trim_name() {
        assertThat(FileNameCleaning.cleanFileName(" a "))
            .isEqualTo("a");
    }

    @Test
    public void cleanFileName__should_replace_all_spaces_with_dash() {
        assertThat(FileNameCleaning.cleanFileName("a    b c\nd"))
            .isEqualTo("a-b-c-d");
    }

    @Test
    public void cleanFileName__should_return_lower_case_only() {
        assertThat(FileNameCleaning.cleanFileName("ABCD"))
            .isEqualTo("abcd");
    }

    @Test
    public void cleanFileName__should_leave_non_latin_characters() {
        assertThat(FileNameCleaning.cleanFileName("你好"))
            .isEqualTo("你好");
    }
}
