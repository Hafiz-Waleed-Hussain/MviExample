package com.uwantolearn.mvi.detail

import com.uwantolearn.mvi.base.MviAction

sealed class DetailActivityAction : MviAction {
    object InitialAction : DetailActivityAction()
    object GetLastStateAction : DetailActivityAction()



    data class EvenOddAction(val v: Int) : DetailActivityAction()
}