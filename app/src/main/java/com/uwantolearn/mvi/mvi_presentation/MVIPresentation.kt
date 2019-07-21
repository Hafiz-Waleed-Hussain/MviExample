package com.uwantolearn.mvi.mvi_presentation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.uwantolearn.mvi.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private val viewModel: MVIPresentationViewModel =
        MVIPresentationViewModel(MVIPresentationRepoImpl())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        intents()
            .let(viewModel::bind)
            .subscribe(::render) { e -> Log.d(this.javaClass.simpleName, "Error" + e.message) }
            .let(compositeDisposable::add)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun intents(): Observable<HomeIntent> =
        Observable.merge(
            listOf<Observable<HomeIntent>>(
                Observable.just(HomeIntent.LoadDataIntent),
                refreshButton.clicks().map { HomeIntent.RefreshIntent },
                randomNumberClick.clicks().map { HomeIntent.GetRandomNumberIntent }
            )
        )

    private fun render(state: HomeViewState): Unit = when (state) {
        HomeViewState.ProgressViewState -> renderLoadingState()
        HomeViewState.FailureViewState -> renderFailureState()
        is HomeViewState.DataViewState -> renderDataState(state.list)
        is HomeViewState.RandomNumberState -> renderRandomNumber(state.randomNumber)
    }

    private fun renderLoadingState() {
        dataTextView.visibility = View.GONE
        refreshButton.visibility = View.GONE
        randomNumberClick.visibility = View.GONE

        progressBar.visibility = View.VISIBLE
    }

    private fun renderFailureState() {
        progressBar.visibility = View.GONE

        randomNumberClick.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
        dataTextView.visibility = View.VISIBLE
        dataTextView.text = getString(R.string.something_went_wrong)
    }

    private fun renderDataState(data: List<String>) {
        progressBar.visibility = View.GONE

        randomNumberClick.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
        dataTextView.visibility = View.VISIBLE
        data.reduce { acc, s -> "$acc\n$s" }.let(dataTextView::setText)
    }

    private fun renderRandomNumber(randomNumber: Int) {
        progressBar.visibility = View.GONE

        refreshButton.visibility = View.VISIBLE
        dataTextView.visibility = View.VISIBLE
        randomNumberClick.text = randomNumber.toString()

    }

}

sealed class HomeIntent {
    object LoadDataIntent : HomeIntent()
    object RefreshIntent : HomeIntent()
    object GetRandomNumberIntent : HomeIntent()
}

sealed class HomeViewState {
    object ProgressViewState : HomeViewState()
    object FailureViewState : HomeViewState()
    data class DataViewState(val list: List<String>) : HomeViewState()
    data class RandomNumberState(val randomNumber: Int) : HomeViewState()

}

