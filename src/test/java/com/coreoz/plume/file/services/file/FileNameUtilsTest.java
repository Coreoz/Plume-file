package com.coreoz.plume.file.services.file;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class FileNameUtilsTest {

	@Test
	public void test_sanitize() {
		assertThat(FileNameUtils.sanitize("éàù  	P$^/'\\\"_1.jpg"))
			.isEqualTo("eau-p_1.jpg");
	}

}
