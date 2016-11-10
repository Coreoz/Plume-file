package com.coreoz.plume.file.gallery.webservices;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.internal.LocalizationMessages;

import com.coreoz.plume.admin.security.permission.WebSessionPermission;
import com.coreoz.plume.file.gallery.services.file.GalleryFileTypeHibernate;
import com.coreoz.plume.file.gallery.services.file.GalleryFileTypeQuerydsl;
import com.coreoz.plume.file.gallery.services.gallery.FileGalleryService;
import com.coreoz.plume.file.gallery.webservices.data.GalleryFileUpload;
import com.coreoz.plume.file.gallery.webservices.permissions.FileGalleryTypeAdmin;
import com.coreoz.plume.file.gallery.webservices.permissions.FileGalleryTypesAdminProvider;
import com.coreoz.plume.file.gallery.webservices.validation.FileGalleryWsError;
import com.coreoz.plume.file.services.file.FileService;
import com.coreoz.plume.file.services.file.FileType;
import com.coreoz.plume.file.services.file.FileTypesProvider;
import com.coreoz.plume.file.services.file.FileUploaded;
import com.coreoz.plume.jersey.errors.Validators;
import com.coreoz.plume.jersey.errors.WsException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/admin/galleries")
@Api(value = "Administer gallery medias")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class FileGalleryAdminWs {

	private final FileService fileService;
	private final FileGalleryService fileGalleryService;
	private final FileType galleryFileType;
	private final Map<String, FileGalleryTypeAdmin> galleryTypeIndex;

	@Inject
	public FileGalleryAdminWs(FileService fileService, FileGalleryService fileGalleryService,
			FileTypesProvider fileTypesProvider,
			FileGalleryTypesAdminProvider fileGalleryTypesAdminProvider) {
		this.fileService = fileService;
		this.fileGalleryService = fileGalleryService;

		this.galleryFileType = fileTypesProvider
			.fileTypesAvailable()
			.stream()
			.filter(fileType ->
				fileType == GalleryFileTypeQuerydsl.PLUME_GALLERY
				|| fileType == GalleryFileTypeHibernate.PLUME_GALLERY
			)
			.findAny()
			.orElseThrow(() -> new RuntimeException(
				"File type GalleryFileTypeQuerydsl.PLUME_GALLERY or "
				+ "GalleryFileTypeHibernate.PLUME_GALLERY should be provided in "
				+ "the FileTypesProvider implementation. See Plume File Gallery readme file."
			));

		this.galleryTypeIndex = fileGalleryTypesAdminProvider
			.fileGalleryTypesAdminAvailable()
			.stream()
			.collect(Collectors.toMap(
				FileGalleryTypeAdmin::name,
				Function.identity()
			));
	}

	@POST
	@ApiOperation(value = "Add a new media to a gallery")
	public void add(GalleryFileUpload galleryFileUpload, @Context WebSessionPermission webSession) {
		Validators.checkRequired("FILE_DATA", galleryFileUpload.getData());
		Validators.checkRequired("FILE_DATA_FILENAME", galleryFileUpload.getData().getFilename());
		Validators.checkRequired("FILE_DATA_DATA", galleryFileUpload.getData().getBase64());
		Validators.checkRequired("GALLERY_TYPE", galleryFileUpload.getGalleryType());

		FileGalleryTypeAdmin galleryType = galleryTypeIndex.get(galleryFileUpload.getGalleryType());

		if(galleryType == null) {
			throw new WsException(FileGalleryWsError.INVALID_GALLERY, galleryFileUpload.getGalleryType());
		}

		if(webSession.getPermissions() == null || !webSession.getPermissions().contains(galleryType.galleryPermission())) {
			throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
		}

		if(galleryType.isAllowedToChangeGallery() != null
			&& !galleryType.isAllowedToChangeGallery().test(webSession, galleryFileUpload.getIdData())) {
			throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
		}

		if(!galleryType.isFilenameAllowed().test(galleryFileUpload.getData().getFilename())) {
			throw new WsException(FileGalleryWsError.INVALID_FILENAME, galleryFileUpload.getData().getFilename());
		}

		FileUploaded fileUploaded = fileService.upload(galleryFileType, galleryFileUpload.getData()).get();
		fileGalleryService.add(
			fileUploaded,
			galleryType,
			galleryFileUpload.getInitialPosition() == null ? 0 : galleryFileUpload.getInitialPosition(),
			galleryFileUpload.getIdData()
		);
	}

}
