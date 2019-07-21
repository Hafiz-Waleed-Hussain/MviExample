package com.uwantolearn.mvi.mvi_presentation

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MVIPresentationViewModel(repo: MVIPresentationRepo) {

    private val actionProcessor = MVIPresentationActionProcessor(repo)

    fun bind(intents: Observable<HomeIntent>): Observable<HomeViewState> =
        intents
            .map(::mapToActions)
            .compose(actionProcessor.processActions)
            .scan(HomeViewState.ProgressViewState, ::reduce)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    private fun mapToActions(intent: HomeIntent): HomeActivityAction = when (intent) {
        HomeIntent.LoadDataIntent -> HomeActivityAction.LoadDataAction
    }

    private fun reduce(previousState: HomeViewState, result: HomeActivityResult): HomeViewState =
        when (result) {
            is HomeActivityResult.DataResult -> HomeViewState.DataViewState(result.data)
            HomeActivityResult.FailureResult -> HomeViewState.FailureViewState
            HomeActivityResult.LoadingResult -> HomeViewState.ProgressViewState
        }
}

sealed class HomeActivityAction {
    object LoadDataAction : HomeActivityAction()
}

sealed class HomeActivityResult {
    data class DataResult(val data: List<String>) : HomeActivityResult()
    object FailureResult : HomeActivityResult()
    object LoadingResult : HomeActivityResult()
}

class MVIPresentationActionProcessor(private val repo: MVIPresentationRepo) {

    private val loadDataActionProcessor =
        ObservableTransformer<HomeActivityAction.LoadDataAction, HomeActivityResult> { action ->
            action.flatMap {
                repo.loadData()
                    .map(HomeActivityResult::DataResult)
                    .cast(HomeActivityResult::class.java)
                    .onErrorReturn { HomeActivityResult.FailureResult }
                    .startWith(HomeActivityResult.LoadingResult)
            }
        }

    val processActions = ObservableTransformer<HomeActivityAction, HomeActivityResult> { action ->
        action.publish { actionSource ->
            actionSource.ofType(HomeActivityAction.LoadDataAction::class.java)
                .compose(loadDataActionProcessor)
        }
    }
}