package com.uwantolearn.mvi.main

import com.uwantolearn.mvi.base.MviViewState

sealed class MainViewState : MviViewState {
    data class WelcomeState(val message: String) : MainViewState()
    object LoadingState : MainViewState()
    object Failure : MainViewState()

    data class IntsDataState(val data: List<Int>) : MainViewState()
}