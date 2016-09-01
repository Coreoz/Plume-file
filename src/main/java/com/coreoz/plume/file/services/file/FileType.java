package com.coreoz.plume.file.services.file;

import com.coreoz.plume.file.db.entities.QFileEntity;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;

public interface FileType {

	String name();

	/**
	 * The entity that contains a column that referenced {@link QFileEntity#id}
	 */
	EntityPathBase<?> getFileEntity();

	/**
	 * The column that has a reference to {@link QFileEntity#id}
	 */
	NumberPath<Long> getJoinColumn();

}
