package com.coreoz.plume.file.gallery.webservices.permissions;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.coreoz.plume.admin.jersey.WebSessionPermission;
import com.coreoz.plume.file.gallery.services.gallery.FileGalleryType;

public interface FileGalleryTypeAdmin extends FileGalleryType {

	String getGalleryPermission();

	BiPredicate<WebSessionPermission, Long> getAllowedToChangeGallery();

	Predicate<String> getFilenameAllowed();

}
