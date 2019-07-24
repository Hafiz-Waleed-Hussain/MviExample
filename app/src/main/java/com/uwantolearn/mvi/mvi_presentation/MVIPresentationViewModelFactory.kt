package com.uwantolearn.mvi.mvi_presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.uwantolearn.mvi.injection.Injector

class MVIPresentationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MVIPresentationViewModel(
        MVIPresentationActionProcessor(Injector.getMVIPresentationRepo())
    ) as T
}