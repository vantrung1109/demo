package digi.kitplay.di.module;

import android.content.Context;

import androidx.core.util.Supplier;
import androidx.lifecycle.ViewModelProvider;

import digi.kitplay.MVVMApplication;
import digi.kitplay.ViewModelProviderFactory;
import digi.kitplay.data.Repository;
import digi.kitplay.di.scope.ActivityScope;
import digi.kitplay.ui.base.activity.BaseActivity;
import digi.kitplay.ui.main.MainViewModel;
import digi.kitplay.ui.main.config.ConfigServerViewModel;
import digi.kitplay.ui.main.connection.ConnectWifiViewModel;
import digi.kitplay.ui.splash.SplashViewModel;
import digi.kitplay.utils.GetInfo;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private final BaseActivity<?, ?> activity;

    public ActivityModule(BaseActivity<?, ?> activity) {
        this.activity = activity;
    }

    @Named("access_token")
    @Provides
    @ActivityScope
    String provideToken(Repository repository){
        return repository.getToken();
    }

    @Named("device_id")
    @Provides
    @ActivityScope
    String provideDeviceId( Context applicationContext){
        return GetInfo.getAll(applicationContext);
//        return "ORG00VL6PO";
    }


    @Provides
    @ActivityScope
    MainViewModel provideMainViewModel(Repository repository, Context application) {
        Supplier<MainViewModel> supplier = () -> new MainViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<MainViewModel> factory = new ViewModelProviderFactory<>(MainViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(MainViewModel.class);
    }

    @Provides
    @ActivityScope
    ConnectWifiViewModel provideConnectWifiViewModel(Repository repository, Context application) {
        Supplier<ConnectWifiViewModel> supplier = () -> new ConnectWifiViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<ConnectWifiViewModel> factory = new ViewModelProviderFactory<>(ConnectWifiViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(ConnectWifiViewModel.class);
    }

    @Provides
    @ActivityScope
    ConfigServerViewModel provideConfigServerViewModel(Repository repository, Context application) {
        Supplier<ConfigServerViewModel> supplier = () -> new ConfigServerViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<ConfigServerViewModel> factory = new ViewModelProviderFactory<>(ConfigServerViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(ConfigServerViewModel.class);
    }

    @Provides
    @ActivityScope
    SplashViewModel provideSplashViewModel(Repository repository, Context application) {
        Supplier<SplashViewModel> supplier = () -> new SplashViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<SplashViewModel> factory = new ViewModelProviderFactory<>(SplashViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(SplashViewModel.class);
    }

}
