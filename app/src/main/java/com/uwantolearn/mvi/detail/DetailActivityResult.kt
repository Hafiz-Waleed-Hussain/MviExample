package com.uwantolearn.mvi.detail

import com.uwantolearn.mvi.base.MviResult

sealed class DetailActivityResult : MviResult {
    data class InitialResult(val message: String) : DetailActivityResult()
    data class EvenOddResult(val v: Int) : DetailActivityResult()
    data class ErrorResult(val error: Throwable) : DetailActivityResult()
    object LoadingResult : DetailActivityResult()
    object GetLastStateResult : DetailActivityResult()

}