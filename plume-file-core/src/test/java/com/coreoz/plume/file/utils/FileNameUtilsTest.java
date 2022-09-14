package com.coreoz.plume.file.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class FileNameUtilsTest {

	@Test
	public void test_sanitize() {
		assertThat(FileNameUtils.cleanExtensionName(".jpg"))
			.isEqualTo(".");
	}

}
