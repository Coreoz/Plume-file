package com.coreoz.plume.file.db;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public abstract class FileDaoDatabaseTest {

	protected abstract FileDaoDatabase fileDao();

	@Test
	public void fileName__should_return_corresponding_filename_if_the_file_exists_and_the_name_is_not_null() {
		assertThat(fileDao().fileName("846c36cc-f973-11e8-8eb2-f2801f1b9fd1")).isEqualTo("file.ext");
	}

	@Test
	public void fileName__should_return_empty_if_the_file_exists_and_the_name_is_null() {
		assertThat(fileDao().fileName("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1")).isEmpty();
	}

	@Test
	public void fileName__should_return_null_if_the_file_does_not_exist() {
		assertThat(fileDao().fileName("8b3cf3de-f973-11e8-8eb2-f2801f1b9fd1")).isNull();
	}

}
