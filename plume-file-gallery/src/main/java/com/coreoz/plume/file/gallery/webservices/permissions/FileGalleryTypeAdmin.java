package com.coreoz.plume.file.gallery.webservices.permissions;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.coreoz.plume.admin.security.permission.WebSessionPermission;
import com.coreoz.plume.file.gallery.services.gallery.FileGalleryType;

public interface FileGalleryTypeAdmin extends FileGalleryType {

	String galleryPermission();

	BiPredicate<WebSessionPermission, Long> isAllowedToChangeGallery();

	Predicate<String> isFilenameAllowed();

}
