package com.uwantolearn.mvi.main

import android.arch.lifecycle.ViewModel
import com.uwantolearn.mvi.base.MviAction
import com.uwantolearn.mvi.base.MviResult
import com.uwantolearn.mvi.base.MviViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

sealed class MainAction : MviAction {
    object WelcomeAction : MainAction()
    object LoadIntsAction : MainAction()
    object GetLastStateAction : MainAction()
}

sealed class MainResult : MviResult {
    data class WelcomeMessage(val message: String) : MainResult()
    data class LoadIntsResult(val data: List<Int>) : MainResult()
    object Loading : MainResult()
    object Failure : MainResult()
    object LastState : MainResult()
}


class MainViewModel : MviViewModel<MainIntent, MainViewState>,
    ViewModel() {

    private val actionProcessor: MainActionProcessor = MainActionProcessor()

    private val intentsSubject = PublishSubject.create<MainIntent>()

    private val statesSubject: PublishSubject<MainViewState> = PublishSubject.create()

    init {
        intentsSubject
            .scan(::initialIntentFilter)
            .map(::actionFromIntent)
            .compose(actionProcessor.actionProcessor)
            .scan(MainViewState.LoadingState, ::reduce)
            .subscribe(statesSubject)

    }


    override fun processIntents(intents: Observable<MainIntent>): Disposable =
        intents.subscribe(intentsSubject::onNext)


    override fun state(): Observable<MainViewState> = statesSubject


    private fun reduce(previousState: MainViewState, result: MainResult): MainViewState = when (result) {
        is MainResult.WelcomeMessage -> MainViewState.WelcomeState(result.message)
        is MainResult.LoadIntsResult -> MainViewState.IntsDataState(result.data)
        MainResult.Loading -> MainViewState.LoadingState
        MainResult.Failure -> MainViewState.Failure
        MainResult.LastState -> previousState
    }


    private fun actionFromIntent(intents: MainIntent): MainAction = when (intents) {
        MainIntent.WelcomeIntent -> MainAction.WelcomeAction
        MainIntent.LoadIntsIntent -> MainAction.LoadIntsAction
        MainIntent.GetLastState -> MainAction.GetLastStateAction
    }

    private fun initialIntentFilter(initialIntent: MainIntent, newIntent: MainIntent): MainIntent =
        if (newIntent is MainIntent.WelcomeIntent) {
            MainIntent.GetLastState
        } else
            newIntent

}
