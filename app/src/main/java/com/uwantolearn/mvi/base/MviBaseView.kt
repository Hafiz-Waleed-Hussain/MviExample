package com.uwantolearn.mvi.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.disposables.CompositeDisposable

abstract class MviBaseView<I : MviIntent, A : MviAction, S : MviViewState, R : MviResult> :
    MviView<I, S>, AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    abstract val viewModel: MviBaseViewModel<I, A, S, R>

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }

    override fun onStart() {
        super.onStart()
        viewModel.state()
            .subscribe(::render) { e ->
                Log.d(
                    this.javaClass.simpleName,
                    "Error" + e.message
                )
            }
            .let(compositeDisposable::add)
        intents()
            .let(viewModel::processIntents)
            .let(compositeDisposable::add)
    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }
}