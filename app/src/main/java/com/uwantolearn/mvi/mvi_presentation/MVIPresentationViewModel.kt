package com.uwantolearn.mvi.mvi_presentation

import android.arch.lifecycle.ViewModel
import com.uwantolearn.mvi.base.MviAction
import com.uwantolearn.mvi.base.MviResult
import com.uwantolearn.mvi.base.MviViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject


class MVIPresentationViewModel(repo: MVIPresentationRepo) : ViewModel(),
    MviViewModel<HomeIntent, HomeViewState> {

    private val actionProcessor = MVIPresentationActionProcessor(repo)
    private val intentsSubject = PublishSubject.create<HomeIntent>()
    private val states = PublishSubject.create<HomeViewState>()

    init {
        intentsSubject
            .scan(::intentFilter)
            .map(::mapToActions)
            .compose(actionProcessor.processActions)
            .scan(HomeViewState(), ::reduce)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(states)
    }

    override fun processIntents(intents: Observable<HomeIntent>): Disposable = intents
        .subscribe(intentsSubject::onNext)

    override fun state(): Observable<HomeViewState> = states.hide()

    override fun onCleared() {
        intentsSubject.onComplete()
        states.onComplete()
        super.onCleared()
    }

    private fun intentFilter(initialIntent: HomeIntent, newIntent: HomeIntent): HomeIntent =
        if (newIntent is HomeIntent.LoadDataIntent)
            HomeIntent.GetLastStateIntent
        else
            newIntent

    private fun mapToActions(intent: HomeIntent): HomeActivityAction = when (intent) {
        HomeIntent.LoadDataIntent, HomeIntent.RefreshIntent -> HomeActivityAction.LoadDataAction
        HomeIntent.GetRandomNumberIntent -> HomeActivityAction.GetRandomNumberAction
        HomeIntent.GetLastStateIntent -> HomeActivityAction.GetLastStateAction
    }

    private fun reduce(previousState: HomeViewState, result: HomeActivityResult): HomeViewState =
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

sealed class HomeActivityAction : MviAction {
    object LoadDataAction : HomeActivityAction()
    object GetRandomNumberAction : HomeActivityAction()
    object GetLastStateAction : HomeActivityAction()
}

sealed class HomeActivityResult : MviResult {
    data class DataResult(val data: List<String>) : HomeActivityResult()
    data class RandomNumber(val randomNumber: Int) : HomeActivityResult()
    object GetLastState : HomeActivityResult()
    object FailureResult : HomeActivityResult()
    object LoadingResult : HomeActivityResult()
}

