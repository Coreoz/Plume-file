package com.coreoz.plume.file.db;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.dao.FileDatabaseDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Optional;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestModule.class)
public class FileDatabaseDaoTest {

	@Inject
	private FileDatabaseDao fileDao;

	@Test
	public void fetch_existing_file_should_return_the_file() {
		Optional<byte[]> file = fileDao.fetch("846c36cc-f973-11e8-8eb2-f2801f1b9fd1");
		Assert.assertTrue(file.isPresent());
		Assert.assertTrue(file.get().length > 0);
	}

	@Test
	public void fetch_unknown_file_should_return_empty() {
		Optional<byte[]> file = fileDao.fetch("unknown");
		Assert.assertFalse(file.isPresent());
	}

	@Test
	public void delete_existing_file_should_return_true() {
		boolean hasBeenDeleted = fileDao.delete("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1");
		Assert.assertTrue(hasBeenDeleted);
	}

	@Test
	public void delete_unknown_file_should_return_false() {
		boolean hasBeenDeleted = fileDao.delete("unknown");
		Assert.assertFalse(hasBeenDeleted);
	}

	@Test
	public void upload_should_not_fail() {
		fileDao.upload(6L, new byte[0]);
		Assert.assertTrue(true);
	}

}
