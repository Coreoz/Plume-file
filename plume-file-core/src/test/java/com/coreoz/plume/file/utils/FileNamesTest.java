package com.coreoz.plume.file.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class FileNamesTest {

	@Test
	public void test_clean_from_string_with_accents_should_return_without_accents() {
		assertThat(FileNames.clean("éçàyt.jpg"))
			.isEqualTo("ecayt.jpg");
	}

	@Test
	public void test_has_accents_with_accents_words_should_return_true() {
		assertThat(FileNames.extensionNameHasAccents("file.jépèèègèhhL"))
			.isTrue();
	}

	@Test
	public void test_has_accents_with_no_accents_words_should_return_false() {
		assertThat(FileNames.extensionNameHasAccents("file.jpg"))
			.isFalse();
	}

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

}
