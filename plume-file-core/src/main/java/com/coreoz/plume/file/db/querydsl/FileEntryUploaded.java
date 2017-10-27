package com.coreoz.plume.file.db.querydsl;

import com.coreoz.plume.file.db.FileEntry;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
public class FileEntryUploaded implements FileEntry {

    private Long id;
    private String filename;
    private String fileType;
    private byte[] data;
}
