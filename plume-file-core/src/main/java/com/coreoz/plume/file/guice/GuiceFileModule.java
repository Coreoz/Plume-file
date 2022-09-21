package com.coreoz.plume.file.guice;

import com.coreoz.plume.scheduler.guice.GuiceSchedulerModule;
import com.google.inject.AbstractModule;

public class GuiceFileModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new GuiceSchedulerModule());
    }

}
