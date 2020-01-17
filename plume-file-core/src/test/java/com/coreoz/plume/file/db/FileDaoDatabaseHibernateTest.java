package com.coreoz.plume.file.db;

import javax.inject.Inject;

import org.junit.runner.RunWith;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.hibernate.FileDaoHibernate;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestDbModule.class)
public class FileDaoDatabaseHibernateTest extends FileDaoDatabaseTest {

	@Inject
	private FileDaoHibernate fileDao;

	@Override
	protected FileDaoDatabase fileDao() {
		return fileDao;
	}

}
