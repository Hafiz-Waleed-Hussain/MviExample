package com.uwantolearn.mvi.base

import io.reactivex.Observable
import io.reactivex.disposables.Disposable


interface MviViewModel<I : MviIntent, S : MviViewState> {
    fun processIntents(intents: Observable<I>) : Disposable
    fun state(): Observable<S>
}