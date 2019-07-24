package com.uwantolearn.mvi.mvi_presentation

import com.uwantolearn.mvi.base.MviBaseViewModel


class MVIPresentationViewModel(actionProcessor: MVIPresentationActionProcessor) :
    MviBaseViewModel<HomeIntent, HomeActivityAction, HomeViewState, HomeActivityResult>(
        actionProcessor,
        HomeViewState()
    ) {

    override fun intentFilter(initialIntent: HomeIntent, newIntent: HomeIntent): HomeIntent =
        if (newIntent is HomeIntent.LoadDataIntent)
            HomeIntent.GetLastStateIntent
        else
            newIntent

    override fun mapToActions(intent: HomeIntent): HomeActivityAction = when (intent) {
        HomeIntent.LoadDataIntent, HomeIntent.RefreshIntent -> HomeActivityAction.LoadDataAction
        HomeIntent.GetRandomNumberIntent -> HomeActivityAction.GetRandomNumberAction
        HomeIntent.GetLastStateIntent -> HomeActivityAction.GetLastStateAction
    }

    override fun reduce(previousState: HomeViewState, result: HomeActivityResult): HomeViewState =
        when (result) {
            is HomeActivityResult.DataResult -> previousState.copy(
                data = result.data,
                inProgress = false
            )
            HomeActivityResult.FailureResult -> previousState.copy(
                isFail = true,
                inProgress = false
            )
            HomeActivityResult.LoadingResult -> previousState.copy(inProgress = true)
            is HomeActivityResult.RandomNumber -> previousState.copy(randomNumber = result.randomNumber)
            HomeActivityResult.GetLastState -> previousState
        }
}

