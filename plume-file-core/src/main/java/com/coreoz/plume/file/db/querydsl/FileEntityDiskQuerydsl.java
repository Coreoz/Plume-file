package com.coreoz.plume.file.db.querydsl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "id_file", "path"})
public class FileEntityDiskQuerydsl {
    private Long id;
    private Long id_file;
    private String path;
}
