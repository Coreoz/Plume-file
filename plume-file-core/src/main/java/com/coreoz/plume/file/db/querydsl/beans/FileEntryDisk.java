package com.coreoz.plume.file.db.querydsl.beans;

import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.FileDao;
import lombok.Value;

@Value(staticConstructor = "of")
public class FileEntryDisk implements FileEntry {
    Long id;
    String uid;
    String fileExtension;
    String fileType;

    @Override
    public String getFileName() {
        return FileDao.fileName(this.uid, this.fileExtension);
    }
}
