package com.coreoz.plume.file.db;

import javax.inject.Inject;

import org.junit.runner.RunWith;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.querydsl.FileDaoDbQuerydsl;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestDbModule.class)
public class FileDaoDbQuerydslTest extends FileDaoTest {

	@Inject
	private FileDaoDbQuerydsl fileDao;

	@Override
	protected FileDao fileDao() {
		return fileDao;
	}

}
