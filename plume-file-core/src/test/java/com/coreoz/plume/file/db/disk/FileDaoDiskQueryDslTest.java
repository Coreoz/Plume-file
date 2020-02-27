package com.coreoz.plume.file.db.disk;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.FileDaoDisk;
import com.coreoz.plume.file.db.FileTestDbModule;
import com.coreoz.plume.file.db.querydsl.disk.FileDaoDiskQuerydsl;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestDbModule.class)
public class FileDaoDiskQueryDslTest extends FileDaoDiskTest {

    @Inject
    private FileDaoDiskQuerydsl fileDao;

    @Override
    protected FileDaoDisk fileDao() {
        return fileDao;
    }
}
