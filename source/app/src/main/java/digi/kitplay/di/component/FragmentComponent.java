package digi.kitplay.di.component;


import digi.kitplay.di.module.FragmentModule;
import digi.kitplay.di.scope.FragmentScope;

import dagger.Component;

@FragmentScope
@Component(modules = {FragmentModule.class},dependencies = AppComponent.class)
public interface FragmentComponent {

}
