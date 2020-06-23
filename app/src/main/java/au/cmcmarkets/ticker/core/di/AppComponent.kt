package au.cmcmarkets.ticker.core.di

import au.cmcmarkets.ticker.CmcApp
import au.cmcmarkets.ticker.core.di.module.*
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        NetworkModule::class,
        ActivityFragmentModule::class,
        ViewModelModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent : AndroidInjector<CmcApp> {

    @Component.Factory
    abstract class Builder : AndroidInjector.Factory<CmcApp>
}