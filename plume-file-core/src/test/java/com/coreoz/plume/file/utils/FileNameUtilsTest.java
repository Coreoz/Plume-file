package com.coreoz.plume.file.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.coreoz.plume.file.utils.FileNameUtils;

public class FileNameUtilsTest {

	@Test
	public void test_sanitize() {
		assertThat(FileNameUtils.sanitize("éàù  	P$^/'\\\"_1.jpg"))
			.isEqualTo("eau-p_1.jpg");
	}

}
