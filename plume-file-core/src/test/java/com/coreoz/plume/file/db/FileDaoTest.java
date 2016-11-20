package com.coreoz.plume.file.db;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public abstract class FileDaoTest {

	protected abstract FileDao fileDao();

	@Test
	public void fileName__should_return_corresponding_filename_if_the_file_exists_and_the_name_is_not_null() {
		assertThat(fileDao().fileName(5L)).isEqualTo("file.ext");
	}

	@Test
	public void fileName__should_return_empty_if_the_file_exists_and_the_name_is_null() {
		assertThat(fileDao().fileName(4L)).isEmpty();
	}

	@Test
	public void fileName__should_return_null_if_the_file_does_not_exist() {
		assertThat(fileDao().fileName(3L)).isNull();
	}

}
