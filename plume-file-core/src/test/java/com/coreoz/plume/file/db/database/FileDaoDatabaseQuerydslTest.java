package com.coreoz.plume.file.db.database;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.FileTestDbModule;
import com.coreoz.plume.file.db.querydsl.FileDao;
import com.coreoz.plume.file.db.querydsl.database.FileDaoDatabaseQuerydsl;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestDbModule.class)
public class FileDaoDatabaseQuerydslTest extends FileDaoDatabaseTest {

	@Inject
	private FileDaoDatabaseQuerydsl fileDao;

	@Override
	protected FileDao fileDao() {
		return fileDao;
	}

}
