package com.uwantolearn.mvi.mvi_presentation

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

class MVIPresentationViewModel(repo: MVIPresentationRepo) {

    private val actionProcessor = MVIPresentationActionProcessor(repo)

    fun bind(intents: Observable<HomeIntent>): Observable<HomeViewState> =
        intents
            .map(::mapToActions)
            .compose(actionProcessor.processActions)
            .scan(HomeViewState.ProgressViewState, ::reduce)
            .observeOn(AndroidSchedulers.mainThread())

    private fun mapToActions(intent: HomeIntent): HomeActivityAction = when (intent) {
        HomeIntent.LoadDataIntent, HomeIntent.RefreshIntent -> HomeActivityAction.LoadDataAction
        HomeIntent.GetRandomNumberIntent -> HomeActivityAction.GetRandomNumberAction
    }

    private fun reduce(previousState: HomeViewState, result: HomeActivityResult): HomeViewState =
        when (result) {
            is HomeActivityResult.DataResult -> HomeViewState.DataViewState(result.data)
            HomeActivityResult.FailureResult -> HomeViewState.FailureViewState
            HomeActivityResult.LoadingResult -> HomeViewState.ProgressViewState
            is HomeActivityResult.RandomNumber -> HomeViewState.RandomNumberState(result.randomNumber)
        }
}

sealed class HomeActivityAction {
    object LoadDataAction : HomeActivityAction()
    object GetRandomNumberAction : HomeActivityAction()
}

sealed class HomeActivityResult {
    data class DataResult(val data: List<String>) : HomeActivityResult()
    data class RandomNumber(val randomNumber: Int) : HomeActivityResult()
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
                    .subscribeOn(Schedulers.io())
                    .startWith(HomeActivityResult.LoadingResult)
            }
        }

    private val randomNumberActionProcessor =
        ObservableTransformer<HomeActivityAction.GetRandomNumberAction, HomeActivityResult> { action ->
            action.map { HomeActivityResult.RandomNumber(Random.nextInt()) }
        }


    val processActions = ObservableTransformer<HomeActivityAction, HomeActivityResult> { action ->
        action.publish { actionSource ->
            Observable.merge(
                actionSource.ofType(HomeActivityAction.LoadDataAction::class.java)
                    .compose(loadDataActionProcessor),
                actionSource.ofType(HomeActivityAction.GetRandomNumberAction::class.java)
                    .compose(randomNumberActionProcessor)
            )
        }
    }
}