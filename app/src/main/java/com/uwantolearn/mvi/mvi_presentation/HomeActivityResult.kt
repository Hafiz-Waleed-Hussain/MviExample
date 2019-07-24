package com.uwantolearn.mvi.mvi_presentation

import com.uwantolearn.mvi.base.MviResult

sealed class HomeActivityResult : MviResult {
    data class DataResult(val data: List<String>) : HomeActivityResult()
    data class RandomNumber(val randomNumber: Int) : HomeActivityResult()
    object GetLastState : HomeActivityResult()
    object FailureResult : HomeActivityResult()
    object LoadingResult : HomeActivityResult()
}