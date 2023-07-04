package com.coreoz.plume.file.db;

import com.querydsl.sql.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFile {
    @Column("user_id")
    private Long userId;
    @Column("unique_name")
    private Long uniqueName;
}
