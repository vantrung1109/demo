package digi.kitplay.di.component;


import android.app.Application;
import android.content.Context;

import digi.kitplay.MVVMApplication;
import digi.kitplay.data.Repository;
import digi.kitplay.di.module.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(MVVMApplication app);

    Repository getRepository();

    Context getContext();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
