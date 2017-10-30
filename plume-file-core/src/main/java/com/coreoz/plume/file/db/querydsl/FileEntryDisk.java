package com.coreoz.plume.file.db.querydsl;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileEntryDisk {
    private final Long id;
    private final String filename;
    private final String fileType;
    private final String path;
}
