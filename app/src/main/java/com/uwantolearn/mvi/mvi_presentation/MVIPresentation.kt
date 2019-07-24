package com.uwantolearn.mvi.mvi_presentation

import android.arch.lifecycle.ViewModelProviders
import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.uwantolearn.mvi.R
import com.uwantolearn.mvi.base.MviBaseView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity :
    MviBaseView<HomeIntent, HomeActivityAction, HomeViewState, HomeActivityResult>() {

    override val viewModel
        get() = ViewModelProviders.of(
            this,
            MVIPresentationViewModelFactory()
        )[MVIPresentationViewModel::class.java]

    override val layoutId: Int
        get() = R.layout.activity_home

    override fun intents(): Observable<HomeIntent> =
        Observable.merge(
            listOf<Observable<HomeIntent>>(
                Observable.just(HomeIntent.LoadDataIntent),
                refreshButton.clicks().map { HomeIntent.RefreshIntent },
                randomNumberClick.clicks().map { HomeIntent.GetRandomNumberIntent })
        )

    override fun render(state: HomeViewState): Unit = when {
        state.inProgress -> renderLoadingState()
        state.isFail -> renderFailureState(state)
        else -> renderDataState(state)
    }

    private fun renderLoadingState() {
        dataTextView.visibility = View.GONE
        refreshButton.visibility = View.GONE
        randomNumberClick.visibility = View.GONE

        progressBar.visibility = View.VISIBLE
    }

    private fun renderFailureState(state: HomeViewState) {
        progressBar.visibility = View.GONE

        randomNumberClick.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
        dataTextView.visibility = View.VISIBLE

        randomNumberClick.text = state.randomNumber.toString()
        dataTextView.text = getString(R.string.something_went_wrong)
    }

    private fun renderDataState(state: HomeViewState) {
        progressBar.visibility = View.GONE

        randomNumberClick.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
        dataTextView.visibility = View.VISIBLE
        state.data.reduce { acc, s -> "$acc\n$s" }.let(dataTextView::setText)
        randomNumberClick.text = state.randomNumber.toString()
    }
}

