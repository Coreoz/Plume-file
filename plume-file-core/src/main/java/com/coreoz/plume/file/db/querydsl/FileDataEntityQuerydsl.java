package com.coreoz.plume.file.db.querydsl;

import com.coreoz.plume.db.querydsl.crud.CrudEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "id_file" })
public class FileDataEntityQuerydsl implements CrudEntity {

    private Long id;
    private Long id_file;
    private byte[] data;
}
