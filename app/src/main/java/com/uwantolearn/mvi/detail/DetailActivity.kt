package com.uwantolearn.mvi.detail

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.uwantolearn.mvi.R
import com.uwantolearn.mvi.base.MviView
import com.uwantolearn.mvi.main.exhaustive
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity(), MviView<DetailActivityIntent, DetailViewState> {

    private lateinit var viewModel: DetailViewModel
    private val compositeDisposable = CompositeDisposable()
    private var value: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        viewModel = ViewModelProviders.of(this)[DetailViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        viewModel.state().subscribe(::render)
            .let(compositeDisposable::add)
        intents()
            .let(viewModel::processIntents)
            .let(compositeDisposable::add)

    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }


    override fun intents(): Observable<DetailActivityIntent> =
        Observable.merge(
            listOf(
                Observable.just(DetailActivityIntent.InitialIntent),
                even_odd.clicks().map { DetailActivityIntent.EvenOdd(value) }
            )
        )

    override fun render(state: DetailViewState) {
        Log.d("MviTesting", state.toString())
        when (state) {
            DetailViewState.Loading -> renderLoadingState()
            is DetailViewState.Initial -> renderInitialState(state.message)
            is DetailViewState.EventOrOdd -> renderEvenOrOddState(state.d)
            is DetailViewState.Error -> renderErrorState(state.t)
        }.exhaustive

    }

    private fun renderLoadingState() {
        progressBar2.visibility = View.VISIBLE
        display.visibility = View.GONE
        display.text = ""
        even_odd.visibility = View.GONE
    }

    private fun renderInitialState(message: String) {
        progressBar2.visibility = View.GONE
        display.visibility = View.VISIBLE
        display.text = message
        even_odd.visibility = View.VISIBLE
    }

    private fun renderEvenOrOddState(d: Int) {
        value = d
        progressBar2.visibility = View.GONE
        display.visibility = View.VISIBLE
        display.text = d.toString()
        even_odd.visibility = View.VISIBLE

    }

    private fun renderErrorState(t: Throwable) {
        progressBar2.visibility = View.GONE
        display.visibility = View.VISIBLE
        display.text = t.toString()
        even_odd.visibility = View.VISIBLE
    }


}
