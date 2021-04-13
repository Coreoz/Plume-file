package com.coreoz.plume.file.db.database;

import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.FileDao;
import com.coreoz.plume.file.db.querydsl.beans.FileEntryDatabase;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class FileDaoDatabaseTest {

	protected abstract FileDao fileDao();

	@Test
	public void fileName__should_return_corresponding_filename_if_the_file_exists_and_the_name_is_not_null() {
		Optional<String> fileName = fileDao().fileName("846c36cc-f973-11e8-8eb2-f2801f1b9fd1");
		assertThat(fileName).hasValue("846c36cc-f973-11e8-8eb2-f2801f1b9fd1.ext");
	}

	@Test
	public void fileName__should_return_not_present_if_the_file_does_not_exist() {
		assertThat(fileDao().fileName("8b3cf3de-f973-11e8-8eb2-f2801f1b9fd1")).isNotPresent();
	}

	@Test
	public void findById__should_not_return_null_if_the_file_exists() {
		Optional<FileEntry> file = fileDao().findById(4L);
		assertThat(file).isPresent();
		FileEntryDatabase fileEntryDatabase = (FileEntryDatabase) file.get();
		assertThat(fileEntryDatabase.getUid()).isNotNull();
		assertThat(fileEntryDatabase.getUid()).isEqualTo("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1");
		assertThat(fileEntryDatabase.getFileName()).isEqualTo("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1.txt");
		assertThat(fileEntryDatabase.getFileType()).isEqualTo("TEST");
		assertThat(fileEntryDatabase.getData()).isNotEmpty();
		assertThat(fileEntryDatabase.getId()).isEqualTo(4L);
	}

	@Test
	public void findById__should_return_null_if_the_file_does_not_exist() {
		Optional<FileEntry> file = fileDao().findById(1L);
		assertThat(file).isNotPresent();
	}

	@Test
	public void findById__should_return_null_if_the_file_does_not_exist_uid() {
		Optional<FileEntry> file = fileDao().findByUid("");
		assertThat(file).isNotPresent();
	}

	@Test
	public void findById__should_not_return_null_if_the_file_exists_uid() {
		Optional<FileEntry> file = fileDao().findByUid("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1");
		assertThat(file).isPresent();
		FileEntryDatabase fileEntryDatabase = (FileEntryDatabase) file.get();
		assertThat(fileEntryDatabase.getUid()).isEqualTo("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1");
		assertThat(fileEntryDatabase.getFileName()).isEqualTo("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1.txt");
		assertThat(fileEntryDatabase.getFileType()).isEqualTo("TEST");
		assertThat(fileEntryDatabase.getData()).isNotEmpty();
		assertThat(fileEntryDatabase.getId()).isEqualTo(4L);
	}

	@Test
	public void findById__should_the_file_be_deleted() {
		Optional<FileEntry> file = fileDao().findByUid("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd2");
		assertThat(file).isPresent();
		assertThat(fileDao().delete("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd2")).isTrue();
		Optional<FileEntry> fileDeleted = fileDao().findByUid("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd2");
		assertThat(fileDeleted).isNotPresent();
	}

	@Test
	public void findById__should_the_file_be_uploaded() {
		FileEntry file = fileDao().upload("TEST", "test", new byte[0]);
		assertThat(file).isNotNull();
		FileEntryDatabase fileEntryDatabase = (FileEntryDatabase) file;
		assertThat(file).isNotNull();
		assertThat(fileEntryDatabase.getUid()).isNotNull();
		assertThat(fileEntryDatabase.getUid()).isNotEmpty();
		assertThat(fileEntryDatabase.getId()).isNotNull();
		assertThat(fileEntryDatabase.getFileName()).isEqualTo(fileEntryDatabase.getUid() + ".test");
		assertThat(fileEntryDatabase.getFileType()).isEqualTo("TEST");
		assertThat(fileEntryDatabase.getData()).isEqualTo(new byte[0]);
	}

}
