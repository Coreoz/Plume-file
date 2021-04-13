package com.coreoz.plume.file.db.disk;

import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.beans.FileEntryDisk;
import com.coreoz.plume.file.db.querydsl.disk.FileDaoDiskQuerydsl;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class FileDaoDiskTest {

    protected abstract FileDaoDiskQuerydsl fileDao();

    @Test
    public void fileName__should_return_corresponding_filename_if_the_file_exists_and_the_name_is_not_null() {
        Optional<String> fileName = fileDao().fileName("846c36cc-f973-11e8-8eb2-f2801f1b9fd1");
        assertThat(fileName).hasValue("846c36cc-f973-11e8-8eb2-f2801f1b9fd1.ext");
    }

    @Test
    public void upload_should_return_corresponding_bean_when_uploaded() {
        FileEntryDisk fileEntryDisk = fileDao().upload("TEST", "txt", null);
        assertThat(fileEntryDisk).isNotNull();
        assertThat(fileEntryDisk.getUid()).isNotNull();
        assertThat(fileEntryDisk.getUid()).isNotEmpty();
        assertThat(fileEntryDisk.getId()).isNotNull();
    }

    @Test
    public void find_by_uid_should_return_corrsponding_bean_when_exists() {
        Optional<FileEntry> fileEntryDiskOptional = fileDao().findByUid("846c36cc-f973-11e8-8eb2-f2801f1b9fd2");
        assertThat(fileEntryDiskOptional).isPresent();
        assertThat(fileEntryDiskOptional.get().getUid()).isNotNull();
        assertThat(fileEntryDiskOptional.get().getUid()).isNotEmpty();
        assertThat(fileEntryDiskOptional.get().getId()).isNotNull();
        assertThat(fileEntryDiskOptional.get().getId()).isEqualTo(6L);
        assertThat(fileEntryDiskOptional.get().getFileName()).isEqualTo("846c36cc-f973-11e8-8eb2-f2801f1b9fd2.ext");
        assertThat(fileEntryDiskOptional.get().getFileType()).isEqualTo("TEST");
    }

    @Test
    public void find_by_id_should_return_corrsponding_bean_when_exists() {
        Optional<FileEntry> fileEntryDisk = fileDao().findById(6L);
        assertThat(fileEntryDisk).isPresent();
        assertThat(fileEntryDisk.get().getUid()).isNotNull();
        assertThat(fileEntryDisk.get().getUid()).isNotEmpty();
        assertThat(fileEntryDisk.get().getUid()).isEqualTo("846c36cc-f973-11e8-8eb2-f2801f1b9fd2");
        assertThat(fileEntryDisk.get().getId()).isNotNull();
        assertThat(fileEntryDisk.get().getFileName()).isEqualTo("846c36cc-f973-11e8-8eb2-f2801f1b9fd2.ext");
        assertThat(fileEntryDisk.get().getFileType()).isEqualTo("TEST");
    }

    @Test
    public void find_by_uid_should_return_null_when_not_exists() {
        Optional<FileEntry> fileEntryDisk = fileDao().findByUid("");
        assertThat(fileEntryDisk).isNotPresent();
    }

    @Test
    public void find_by_id_should_return_null_when_not_exists() {
        Optional<FileEntry> fileEntryDisk = fileDao().findById(1L);
        assertThat(fileEntryDisk).isNotPresent();
    }

    @Test
    public void delete_should_the_file_be_deleted() {
        Optional<FileEntry> fileEntryDisk = fileDao().findByUid("846c36cc-f973-11e8-8eb2-f2801f1b9fd3");
        assertThat(fileEntryDisk).isPresent();
        assertThat(fileDao().delete("846c36cc-f973-11e8-8eb2-f2801f1b9fd3")).isTrue();
        Optional<FileEntry> fileDeleted = fileDao().findByUid("846c36cc-f973-11e8-8eb2-f2801f1b9fd3");
        assertThat(fileDeleted).isNotPresent();
    }
}
