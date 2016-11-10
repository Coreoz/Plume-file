package com.coreoz.plume.file.gallery.db.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.coreoz.plume.db.hibernate.utils.HibernateIdGenerator;
import com.coreoz.plume.file.gallery.db.FileGalleryRaw;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@Entity
@Table(name = "plm_file_gallery")
public class FileGalleryHibernate implements FileGalleryRaw {

	@GeneratedValue(generator = HibernateIdGenerator.NAME)
	@Column(name = "id_file")
	@Id
	private Long idFile;
	@Column(name = "id_data")
	private Long idData;
	@Column(name = "gallery_type")
	private String galleryType;
	private Integer position;

}
