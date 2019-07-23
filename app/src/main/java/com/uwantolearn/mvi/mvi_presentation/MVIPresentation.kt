package com.uwantolearn.mvi.mvi_presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.uwantolearn.mvi.R
import com.uwantolearn.mvi.base.MviIntent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*

class MVIPresentationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MVIPresentationViewModel(
        MVIPresentationRepoImpl()
    ) as T
}

class HomeActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var viewModel: MVIPresentationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewModel =
            ViewModelProviders.of(
                this,
                MVIPresentationViewModelFactory()
            )[MVIPresentationViewModel::class.java]


        viewModel.state()
            .subscribe(::render) { e -> Log.d(this.javaClass.simpleName, "Error" + e.message) }
            .let(compositeDisposable::add)
        intents()
            .let(viewModel::processIntents)
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
                randomNumberClick.clicks().map { HomeIntent.GetRandomNumberIntent }))

    private fun render(state: HomeViewState): Unit = when {
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

sealed class HomeIntent : MviIntent {
    object LoadDataIntent : HomeIntent()
    object RefreshIntent : HomeIntent()
    object GetRandomNumberIntent : HomeIntent()
    object GetLastStateIntent : HomeIntent()
}

data class HomeViewState(
    val inProgress: Boolean = true,
    val isFail: Boolean = false,
    val data: List<String> = listOf(),
    val randomNumber: Int = 0
)

