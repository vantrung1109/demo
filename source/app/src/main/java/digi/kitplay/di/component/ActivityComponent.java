package digi.kitplay.di.component;

import digi.kitplay.di.module.ActivityModule;
import digi.kitplay.di.scope.ActivityScope;
import digi.kitplay.ui.main.MainActivity;
import digi.kitplay.ui.main.config.ConfigServerActivity;
import digi.kitplay.ui.main.connection.ConnectWifiActivity;

import dagger.Component;
import digi.kitplay.ui.splash.SplashActivity;

@ActivityScope
@Component(modules = {ActivityModule.class}, dependencies = AppComponent.class)
public interface ActivityComponent {
    void inject(MainActivity activity);

    void inject(ConnectWifiActivity activity);

    void inject(ConfigServerActivity configServerActivity);
    void inject(SplashActivity splashActivity);
}

