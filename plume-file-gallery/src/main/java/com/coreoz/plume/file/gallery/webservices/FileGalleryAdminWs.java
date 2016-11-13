package com.coreoz.plume.file.gallery.webservices;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.internal.LocalizationMessages;

import com.coreoz.plume.admin.security.permission.WebSessionPermission;
import com.coreoz.plume.file.gallery.services.file.GalleryFileTypeHibernate;
import com.coreoz.plume.file.gallery.services.file.GalleryFileTypeQuerydsl;
import com.coreoz.plume.file.gallery.services.gallery.FileGallery;
import com.coreoz.plume.file.gallery.services.gallery.FileGalleryService;
import com.coreoz.plume.file.gallery.webservices.data.FileGalleryPositionAdmin;
import com.coreoz.plume.file.gallery.webservices.data.FileGalleryUpload;
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

	@GET
	@Path("{galleryType}/{idData}")
	@ApiOperation(value = "Fetch gallery medias")
	public List<FileGallery> fetch(@PathParam("galleryType") String galleryTypeParam,
			@PathParam("idData") Long idData, @Context WebSessionPermission webSession) {
		return fileGalleryService.fetch(
			validateAccessAndParseGallery(galleryTypeParam, idData, webSession),
			idData
		);
	}

	@POST
	@Path("{galleryType}/{idData}")
	@ApiOperation(value = "Add a new media to a gallery")
	public void add(@PathParam("galleryType") String galleryTypeParam, @PathParam("idData") Long idData,
			FileGalleryUpload galleryFileUpload, @Context WebSessionPermission webSession) {
		Validators.checkRequired("FILE_DATA", galleryFileUpload.getData());
		Validators.checkRequired("FILE_DATA_FILENAME", galleryFileUpload.getData().getFilename());
		Validators.checkRequired("FILE_DATA_DATA", galleryFileUpload.getData().getBase64());

		FileGalleryTypeAdmin galleryType = validateAccessAndParseGallery(galleryTypeParam, idData, webSession);

		if(!galleryType.isFilenameAllowed().test(galleryFileUpload.getData().getFilename())) {
			throw new WsException(FileGalleryWsError.INVALID_FILENAME, galleryFileUpload.getData().getFilename());
		}

		FileUploaded fileUploaded = fileService.upload(galleryFileType, galleryFileUpload.getData()).get();
		fileGalleryService.add(
			fileUploaded,
			galleryType,
			galleryFileUpload.getInitialPosition() == null ? 0 : galleryFileUpload.getInitialPosition(),
			idData
		);
	}

	@DELETE
	@Path("{galleryType}/{idFile}/{idData}")
	@ApiOperation(value = "Delete a media from a gallery")
	public void delete(@PathParam("galleryType") String galleryTypeParam,
			@PathParam("idFile") Long idFile, @PathParam("idData") Long idData,
			@Context WebSessionPermission webSession) {
		// TODO to implement
	}

	@PUT
	@Path("{galleryType}/{idData}")
	@ApiOperation(value = "Reorder gallery medias")
	public void updatePositions(@PathParam("galleryType") String galleryTypeParam,
			@PathParam("idData") Long idData, List<FileGalleryPositionAdmin> medias,
			@Context WebSessionPermission webSession) {
		// TODO to implement
	}

	// internal

	private FileGalleryTypeAdmin validateAccessAndParseGallery(String galleryTypeParam, Long idData,
			WebSessionPermission webSession) {
		Validators.checkRequired("GALLERY_TYPE", galleryTypeParam);

		FileGalleryTypeAdmin galleryType = galleryTypeIndex.get(galleryTypeParam);

		if(galleryType == null) {
			throw new WsException(FileGalleryWsError.INVALID_GALLERY, galleryTypeParam);
		}

		if(webSession.getPermissions() == null
			|| !webSession.getPermissions().contains(galleryType.galleryPermission())) {
			throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
		}

		if(galleryType.isAllowedToChangeGallery() != null
			&& !galleryType.isAllowedToChangeGallery().test(webSession, idData)) {
			throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
		}

		return galleryType;
	}

}
