package com.coreoz.plume.file.db.querydsl.database;

import com.coreoz.plume.db.querydsl.crud.CrudEntity;
import com.querydsl.sql.Column;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "idFile" })
public class FileDatabaseEntityQuerydsl implements CrudEntity {
    private Long id;
    @Column("ID_FILE")
    private Long idFile;
    private byte[] data;
}
