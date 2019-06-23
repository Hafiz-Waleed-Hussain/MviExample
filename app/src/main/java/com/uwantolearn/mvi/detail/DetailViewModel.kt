package com.uwantolearn.mvi.detail

import android.arch.lifecycle.ViewModel
import com.uwantolearn.mvi.base.MviViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject


class DetailViewModel(private val useCase: DetailUseCase) : MviViewModel<DetailActivityIntent, DetailViewState>, ViewModel() {


    private val actionProcessor: DetailActivityProcessor = DetailActivityProcessor(useCase)
    private val intentsSubject = PublishSubject.create<DetailActivityIntent>()
    private val states = PublishSubject.create<DetailViewState>()


    init {
        intentsSubject
            .scan(::intentFilter)
            .map(::actionFromIntent)
            .compose(actionProcessor.actionsProcessor)
            .scan(DetailViewState.Loading, ::reduce)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(states)
    }

    private fun intentFilter(
        initialIntent: DetailActivityIntent,
        newIntent: DetailActivityIntent
    ): DetailActivityIntent =
        if (newIntent is DetailActivityIntent.InitialIntent)
            DetailActivityIntent.GetLastStateIntent
        else
            newIntent

    override fun processIntents(intents: Observable<DetailActivityIntent>): Disposable =
        intents.subscribe(intentsSubject::onNext)

    override fun state(): Observable<DetailViewState> = states.hide()


    private fun actionFromIntent(intent: DetailActivityIntent): DetailActivityAction = when (intent) {
        DetailActivityIntent.InitialIntent -> DetailActivityAction.InitialAction
        is DetailActivityIntent.EvenOdd -> DetailActivityAction.EvenOddAction(intent.v)
        DetailActivityIntent.GetLastStateIntent -> DetailActivityAction.GetLastStateAction
    }

    private fun reduce(previousState: DetailViewState, result: DetailActivityResult): DetailViewState = when (result) {
        is DetailActivityResult.InitialResult -> DetailViewState.Initial(result.message)
        is DetailActivityResult.EvenOddResult -> DetailViewState.EventOrOdd(result.v)
        is DetailActivityResult.ErrorResult -> DetailViewState.Error(result.error)
        DetailActivityResult.LoadingResult -> DetailViewState.Loading
        DetailActivityResult.GetLastStateResult -> previousState
    }

}