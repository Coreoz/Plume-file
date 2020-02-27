package com.coreoz.plume.file.db.disk;

import com.coreoz.plume.file.db.FileDaoDisk;
import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.beans.FileEntryDisk;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class FileDaoDiskTest {

    protected abstract FileDaoDisk fileDao();

    @Test
    public void fileName__should_return_corresponding_filename_if_the_file_exists_and_the_name_is_not_null() {
        assertThat(fileDao().fileName("846c36cc-f973-11e8-8eb2-f2801f1b9fd1")).isEqualTo("file.ext");
    }

    @Test
    public void upload_should_return_corresponding_bean_when_uploaded() {
        FileEntryDisk fileEntryDisk = fileDao().upload("TEST", "file_upload.txt", "/path/file_upload.txt");
        assertThat(fileEntryDisk).isNotNull();
        assertThat(fileEntryDisk.getUid()).isNotNull();
        assertThat(fileEntryDisk.getUid()).isNotEmpty();
        assertThat(fileEntryDisk.getId()).isNotNull();
    }

    @Test
    public void find_by_uid_should_return_corrsponding_bean_when_exists() {
        FileEntryDisk fileEntryDisk = fileDao().findByUid("846c36cc-f973-11e8-8eb2-f2801f1b9fd2");
        assertThat(fileEntryDisk).isNotNull();
        assertThat(fileEntryDisk.getUid()).isNotNull();
        assertThat(fileEntryDisk.getUid()).isNotEmpty();
        assertThat(fileEntryDisk.getId()).isNotNull();
        assertThat(fileEntryDisk.getId()).isEqualTo(6L);
        assertThat(fileEntryDisk.getFilename()).isEqualTo("file.ext");
        assertThat(fileEntryDisk.getFileType()).isEqualTo("TEST");
    }

    @Test
    public void find_by_id_should_return_corrsponding_bean_when_exists() {
        FileEntryDisk fileEntryDisk = fileDao().findById(6L);
        assertThat(fileEntryDisk).isNotNull();
        assertThat(fileEntryDisk.getUid()).isNotNull();
        assertThat(fileEntryDisk.getUid()).isNotEmpty();
        assertThat(fileEntryDisk.getUid()).isEqualTo("846c36cc-f973-11e8-8eb2-f2801f1b9fd2");
        assertThat(fileEntryDisk.getId()).isNotNull();
        assertThat(fileEntryDisk.getFilename()).isEqualTo("file.ext");
        assertThat(fileEntryDisk.getFileType()).isEqualTo("TEST");
    }

    @Test
    public void find_by_uid_should_return_null_when_not_exists() {
        FileEntryDisk fileEntryDisk = fileDao().findByUid("");
        assertThat(fileEntryDisk).isNull();
    }

    @Test
    public void find_by_id_should_return_null_when_not_exists() {
        FileEntryDisk fileEntryDisk = fileDao().findById(1L);
        assertThat(fileEntryDisk).isNull();
    }

    @Test
    public void delete_should_the_file_be_deleted() {
        FileEntryDisk file = fileDao().findByUid("846c36cc-f973-11e8-8eb2-f2801f1b9fd3");
        assertThat(file).isNotNull();
        assertThat(fileDao().delete("846c36cc-f973-11e8-8eb2-f2801f1b9fd3")).isEqualTo("846c36cc-f973-11e8-8eb2-f2801f1b9fd3");
        FileEntryDisk fileDeleted = fileDao().findByUid("846c36cc-f973-11e8-8eb2-f2801f1b9fd3");
        assertThat(fileDeleted).isNull();
    }
}
