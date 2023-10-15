package api.luisangeldd.mediapicker.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <vm: ViewModel> viewModelFactory (
    initializer: () -> vm
) : ViewModelProvider.Factory{
    return object : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return initializer() as T
        }
    }
}