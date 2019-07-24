package com.uwantolearn.mvi.mvi_presentation

import com.uwantolearn.mvi.base.MviIntent

sealed class HomeIntent : MviIntent {
    object LoadDataIntent : HomeIntent()
    object RefreshIntent : HomeIntent()
    object GetRandomNumberIntent : HomeIntent()
    object GetLastStateIntent : HomeIntent()
}