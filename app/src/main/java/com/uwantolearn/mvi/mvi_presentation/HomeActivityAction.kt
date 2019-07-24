package com.uwantolearn.mvi.mvi_presentation

import com.uwantolearn.mvi.base.MviAction

sealed class HomeActivityAction : MviAction {
    object LoadDataAction : HomeActivityAction()
    object GetRandomNumberAction : HomeActivityAction()
    object GetLastStateAction : HomeActivityAction()
}