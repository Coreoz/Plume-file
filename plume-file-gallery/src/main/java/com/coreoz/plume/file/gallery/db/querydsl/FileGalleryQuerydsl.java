package com.coreoz.plume.file.gallery.db.querydsl;

import com.coreoz.plume.file.gallery.db.FileGalleryRaw;
import com.querydsl.sql.Column;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode(of = "idFile")
@ToString(of = { "idFile", "idData", "galleryType", "position" })
public class FileGalleryQuerydsl implements FileGalleryRaw {

	@Column("GALLERY_TYPE")
	private String galleryType;

	@Column("ID_DATA")
	private Long idData;

	@Column("ID_FILE")
	private Long idFile;

	@Column("POSITION")
	private Integer position;

}
