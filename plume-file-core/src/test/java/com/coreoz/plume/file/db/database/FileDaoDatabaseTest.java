package com.coreoz.plume.file.db.database;

import com.coreoz.plume.file.db.FileDaoDatabase;
import com.coreoz.plume.file.db.FileEntry;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

	@Test
	public void findById__should_not_return_null_if_the_file_exists() {
		FileEntry file = fileDao().findById(4L);
		assertThat(file.getUid()).isNotNull();
		assertThat(file.getUid()).isEqualTo("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1");
		assertThat(file.getFilename()).isNull();
		assertThat(file.getFileType()).isEqualTo("TEST");
		assertThat(file.getData()).isNotEmpty();
		assertThat(file.getId()).isEqualTo(4L);
	}

	@Test
	public void findById__should_return_null_if_the_file_does_not_exist() {
		FileEntry file = fileDao().findById(1L);
		assertThat(file).isNull();
	}

	@Test
	public void findById__should_return_null_if_the_file_does_not_exist_uid() {
		FileEntry file = fileDao().findByUid("");
		assertThat(file).isNull();
	}

	@Test
	public void findById__should_not_return_null_if_the_file_exists_uid() {
		FileEntry file = fileDao().findByUid("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1");
		assertThat(file.getUid()).isEqualTo("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1");
		assertThat(file.getFilename()).isNull();
		assertThat(file.getFileType()).isEqualTo("TEST");
		assertThat(file.getData()).isNotEmpty();
		assertThat(file.getId()).isEqualTo(4L);
	}

	@Test
	public void findById__should_the_file_be_deleted() {
		FileEntry file = fileDao().findByUid("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd2");
		assertThat(file).isNotNull();
		assertThat(fileDao().delete("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd2")).isEqualTo("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd2");
		FileEntry fileDeleted = fileDao().findByUid("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd2");
		assertThat(fileDeleted).isNull();
	}

	@Test
	public void findById__should_the_file_be_uploaded() {
		FileEntry file = fileDao().upload("TEST", new byte[0], "filename.test");
		assertThat(file).isNotNull();
		assertThat(file.getUid()).isNotNull();
		assertThat(file.getUid()).isNotEmpty();
		assertThat(file.getId()).isNotNull();
		assertThat(file.getFilename()).isEqualTo("filename.test");
		assertThat(file.getFileType()).isEqualTo("TEST");
		assertThat(file.getData()).isEqualTo(new byte[0]);
	}

}
