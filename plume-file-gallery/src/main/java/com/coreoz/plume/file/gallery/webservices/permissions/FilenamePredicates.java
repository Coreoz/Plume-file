package com.coreoz.plume.file.gallery.webservices.permissions;

import java.util.function.Predicate;

import com.coreoz.plume.file.utils.FileNameUtils;

public class FilenamePredicates {

	public static Predicate<String> withMimeType(Predicate<String> mimeTypeChecker) {
		return filename -> {
			String mimeType = FileNameUtils.guessMimeType(filename);
			return mimeType != null && mimeTypeChecker.test(mimeType);
		};
	}

	public static Predicate<String> imagesOnly() {
		return withMimeType(mimeType -> mimeType.startsWith("image"));
	}

}
