package com.uwantolearn.mvi.base

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

abstract class MviBaseViewModel<I : MviIntent, A : MviAction, S : MviViewState, R : MviResult>(
    actionProcessor: MviBaseActionProcessor<A, R>,
    initialState: S
) :
    ViewModel(), MviViewModel<I, S> {

    private val intentsSubject = PublishSubject.create<I>()
    private val states = PublishSubject.create<S>()

    init {
        intentsSubject
            .scan(::intentFilter)
            .map(::mapToActions)
            .compose(actionProcessor.processActions)
            .scan(initialState, ::reduce)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(states)
    }

    override fun processIntents(intents: Observable<I>): Disposable = intents
        .subscribe(intentsSubject::onNext)


    override fun state(): Observable<S> = states.hide()

    override fun onCleared() {
        intentsSubject.onComplete()
        states.onComplete()
        super.onCleared()
    }

    abstract fun intentFilter(initialIntent: I, newIntent: I): I

    abstract fun mapToActions(intent: I): A

    abstract fun reduce(previousState: S, result: R): S


}