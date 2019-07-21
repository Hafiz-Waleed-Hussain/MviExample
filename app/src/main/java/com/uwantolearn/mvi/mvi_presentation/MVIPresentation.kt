package com.uwantolearn.mvi.mvi_presentation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import com.uwantolearn.mvi.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getIntents()
            .scan(HomeViewState(0), ::reduce)
            .subscribe(::render)
            .let(compositeDisposable::add)
    }

    private fun getIntents(): Observable<HomeIntent> =
        Observable.merge(
            listOf<Observable<HomeIntent>>(
                incrementButton.clicks().map { HomeIntent.IncrementIntent },
                decrementButton.clicks().map { HomeIntent.DecrementIntent }
            )
        )

    private fun reduce(previousState: HomeViewState, intent: HomeIntent): HomeViewState =
        when (intent) {
            HomeIntent.IncrementIntent -> previousState.copy(counter = previousState.counter + 1)
            HomeIntent.DecrementIntent -> previousState.copy(counter = previousState.counter - 1)
        }


    private fun render(state: HomeViewState) {
        state.counter.toString()
            .let(counterTextView::setText)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }


}

sealed class HomeIntent {
    object IncrementIntent : HomeIntent()
    object DecrementIntent : HomeIntent()
}

data class HomeViewState(val counter: Int)

