package com.uwantolearn.mvi.main

import com.uwantolearn.mvi.base.MviIntent

sealed class MainIntent : MviIntent {
    object WelcomeIntent : MainIntent()
    object LoadIntsIntent : MainIntent()
    object GetLastState : MainIntent()
}