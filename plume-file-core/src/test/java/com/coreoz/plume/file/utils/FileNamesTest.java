package com.coreoz.plume.file.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FileNamesTest {
	@Test
	public void test_get_extension_from_jpg_should_return_jpg() {
		assertThat(FileNames.parseFileNameExtension("toto.jpg"))
			.isEqualTo("jpg");
	}

	@Test
	public void test_get_extension_from_no_extension_should_return_empty() {
		assertThat(FileNames.parseFileNameExtension("toto"))
			.isNull();
	}

	@Test
	public void test_clean_extension_from_jpg_should_return_jpg() {
		assertThat(FileNames.cleanExtensionName(".jpg"))
			.isEqualTo("jpg");
	}

	@Test
	public void cleanExtensionName__verify_that_non_basic_chars_are_removed() {
		assertThat(FileNames.cleanExtensionName(" éi+$.	\np"))
			.isEqualTo("ip");
	}
}
