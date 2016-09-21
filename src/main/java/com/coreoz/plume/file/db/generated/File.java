package com.coreoz.plume.file.db.generated;

import javax.annotation.Generated;
import com.querydsl.sql.Column;

/**
 * File is a Querydsl bean type
 */
@Generated("com.coreoz.plume.db.querydsl.generation.IdBeanSerializer")
public class File extends com.coreoz.plume.db.querydsl.crud.CrudEntityQuerydsl {

    @Column("DATA")
    private byte[] data;

    @Column("FILENAME")
    private String filename;

    @Column("FILE_TYPE")
    private String fileType;

    @Column("ID")
    private Long id;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (id == null) {
            return super.equals(o);
        }
        if (!(o instanceof File)) {
            return false;
        }
        File obj = (File) o;
        return id.equals(obj.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return super.hashCode();
        }
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "File#" + id;
    }

}

