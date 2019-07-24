package com.uwantolearn.mvi.base

import io.reactivex.ObservableTransformer

abstract class MviBaseActionProcessor<A : MviAction, R : MviResult> {

    abstract val processActions: ObservableTransformer<A, R>
}