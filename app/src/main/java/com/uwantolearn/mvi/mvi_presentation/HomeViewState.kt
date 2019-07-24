package com.uwantolearn.mvi.mvi_presentation

import com.uwantolearn.mvi.base.MviViewState

data class HomeViewState(
    val inProgress: Boolean = true,
    val isFail: Boolean = false,
    val data: List<String> = listOf(),
    val randomNumber: Int = 0
) : MviViewState