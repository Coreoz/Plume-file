package com.coreoz.plume.file.db.querydsl;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileEntityDiskQuerydsl {
    private Long id;
    private Long id_file;
    private String path;
}
