package com.coreoz.plume.file.db.daos;

import java.util.List;

import javax.persistence.EntityManager;

import com.coreoz.plume.file.db.entities.FileEntity;

public interface FileDao {

	FileEntity upload(byte[] fileData, String fileName, EntityManager transaction);

	void delete(Long fileId, EntityManager transaction);

	List<FileWithName> findFileNames(List<Long> fileIds);

	String fileName(Long fileId);

	FileEntity findById(Long fileId);

}
