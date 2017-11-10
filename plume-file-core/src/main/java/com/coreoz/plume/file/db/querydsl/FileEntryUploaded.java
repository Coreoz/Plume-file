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

    private final Long id;
    private final String filename;
    private final String fileType;
    private final byte[] data;

}
