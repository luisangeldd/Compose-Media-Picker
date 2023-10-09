package api.luisangeldd.mediapicker.core

import android.app.Application
import api.luisangeldd.mediapicker.data.repository.hilt.HiltMediaPickerUseCase
import api.luisangeldd.mediapicker.data.repository.hilt.HiltMediaPickerRepo
import api.luisangeldd.mediapicker.data.repository.hilt.HiltMediaPickerRepoImpl
import api.luisangeldd.mediapicker.data.repository.koin.KoinMediaPickerRepo
import api.luisangeldd.mediapicker.data.repository.koin.KoinMediaPickerRepoImpl
import api.luisangeldd.mediapicker.data.repository.koin.KoinMediaPickerUseCase
import api.luisangeldd.mediapicker.ui.Koin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import javax.inject.Singleton

object AppModuleMediaPicker {
    @Module
    @InstallIn(SingletonComponent::class)
    class MediaPickerModuleHilt {
        @Provides
        @Singleton
        fun provideMediaPickerRepo(app: Application): HiltMediaPickerRepo = HiltMediaPickerRepoImpl(app.applicationContext)

        @Provides
        @Singleton
        fun provideMediaPickerUseCase(hiltMediaPickerRepo: HiltMediaPickerRepo): HiltMediaPickerUseCase = HiltMediaPickerUseCase(hiltMediaPickerRepo)
    }
    val MediaPickerModuleKoin = module {
        single<KoinMediaPickerRepo> { KoinMediaPickerRepoImpl(get()) }
        single { KoinMediaPickerUseCase(get()) }
        viewModel { Koin(get()) }
    }
}