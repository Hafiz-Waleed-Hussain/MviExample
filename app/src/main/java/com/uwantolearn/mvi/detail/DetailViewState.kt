package com.uwantolearn.mvi.detail

import com.uwantolearn.mvi.base.MviViewState

sealed class DetailViewState : MviViewState {

    object Loading : DetailViewState()
    data class Initial(val message: String) : DetailViewState()
    data class EventOrOdd(val d: Int) : DetailViewState()
    data class Error(val t: Throwable) : DetailViewState()
}