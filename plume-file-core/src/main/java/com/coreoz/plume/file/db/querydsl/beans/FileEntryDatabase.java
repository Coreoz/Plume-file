package com.coreoz.plume.file.db.querydsl.beans;

import com.coreoz.plume.file.db.FileEntry;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileEntryDatabase implements FileEntry {
    private final Long id;
    private final String uid;
    private final String filename;
    private final String fileType;
    private final byte[] data;
}
