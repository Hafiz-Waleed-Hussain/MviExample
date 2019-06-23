package com.uwantolearn.mvi.detail

import com.uwantolearn.mvi.base.MviIntent

sealed class DetailActivityIntent : MviIntent {
    object InitialIntent : DetailActivityIntent()
    object GetLastStateIntent : DetailActivityIntent()


    data class EvenOdd(val v: Int) : DetailActivityIntent()
}