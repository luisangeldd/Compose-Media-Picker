package api.luisangeldd.mediapicker.h.di

import android.app.Application
import api.luisangeldd.mediapicker.h.data.MediaPickerUseCase
import api.luisangeldd.mediapicker.h.data.repository.MediaPickerRepo
import api.luisangeldd.mediapicker.h.data.repository.MediaPickerRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideMediaPickerRepo(app: Application): MediaPickerRepo = MediaPickerRepoImpl(app.applicationContext)

    /*@Provides
    @Singleton
    fun provideMediaPickerUseCase(mediaPickerRepo: MediaPickerRepo): MediaPickerUseCase =
        MediaPickerUseCase(mediaPickerRepo)

     */
}