package com.coreoz.plume.file.db.beans;

import com.coreoz.plume.db.querydsl.crud.CrudEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.querydsl.sql.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FileDataQueryDsl implements CrudEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    @Column("id")
    private Long id;
    @Column("data")
    private byte[] data;
}
