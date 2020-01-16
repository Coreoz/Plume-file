package com.coreoz.plume.file.db.querydsl.disk;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode(of = "idFile")
@ToString
public class FileEntityDiskQuerydsl {
    private Long idFile;
    private String path;
}
