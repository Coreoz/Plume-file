package com.coreoz.plume.file.db.beans;

import com.querydsl.sql.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FileDataQueryDsl {
    @Column("unique_name")
    private String uniqueName;
    @Column("data")
    private byte[] data;
}
