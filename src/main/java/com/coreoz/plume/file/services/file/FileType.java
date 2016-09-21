package com.coreoz.plume.file.services.file;

import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.RelationalPathBase;

public interface FileType {

	String name();

	/**
	 * The entity that contains a column that referenced {@link QFileEntity#id}
	 */
	RelationalPathBase<?> getFileEntity();

	/**
	 * The column that has a reference to {@link QFileEntity#id}
	 */
	NumberPath<Long> getJoinColumn();

}
