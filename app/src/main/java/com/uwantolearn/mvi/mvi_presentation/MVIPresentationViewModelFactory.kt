package com.uwantolearn.mvi.mvi_presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class MVIPresentationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MVIPresentationViewModel(
        MVIPresentationActionProcessor(MVIPresentationRepoImpl())
    ) as T
}