package com.example.eazyremote.di.module

import android.app.Application
import com.example.eazyremote.data.local.database.CarDatabase
import com.example.eazyremote.data.local.sharedPref.SharedPrefData
import com.example.eazyremote.data.remote.impl.SomeDataRemoteDataSource
import com.example.eazyremote.data.remote.impl.TcpClientImpl
import com.example.eazyremote.di.utils.CoroutineScropeIO
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module(includes = [
    ViewModelModule::class,
    CoreDataModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideTcpClientImpl() = TcpClientImpl()

    @Singleton
    @Provides
    fun provideSomeDataRemoteDataSource(
        service: TcpClientImpl
    ) = SomeDataRemoteDataSource(service)

    @Singleton
    @Provides
    fun provideSharedPref(app : Application) = SharedPrefData(app)

    @Singleton
    @Provides
    fun provideDb(app: Application) = CarDatabase.getDatabase(app)

    @Singleton
    @Provides
    fun provideCarCharacteristicsDao(db: CarDatabase) = db.carDao()

    @CoroutineScropeIO
    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.IO)

}