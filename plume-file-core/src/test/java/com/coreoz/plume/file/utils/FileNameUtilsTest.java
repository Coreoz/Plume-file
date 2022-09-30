package com.coreoz.plume.file.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class FileNameUtilsTest {

	@Test
	public void test_get_extension_from_jpg_should_return_jpg() {
		assertThat(FileNameUtils.getExtensionFromFilename("toto.jpg"))
			.isEqualTo("jpg");
	}

	@Test
	public void test_clean_extension_from_jpg_should_return_jpg() {
		assertThat(FileNameUtils.cleanExtensionName(".jpg"))
			.isEqualTo("jpg");
	}

}
