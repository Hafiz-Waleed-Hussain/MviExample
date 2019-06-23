package com.uwantolearn.mvi.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.lifecycle.ViewModelStore
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.uwantolearn.mvi.R
import com.uwantolearn.mvi.base.MviView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), MviView<MainIntent, MainViewState> {

    lateinit var mainViewModel: MainViewModel
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        mainViewModel
            .state()
            .subscribe(::render) { println(it) }
            .let(compositeDisposable::add)
        mainViewModel.processIntents(intents())
            .let(compositeDisposable::add)

    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }


    override fun intents(): Observable<MainIntent> =
        Observable.merge(
            listOf(
                welcomeIntent(),
                loadIntsIntent()
            )
        )


    private fun welcomeIntent(): Observable<MainIntent> =
        Observable.just(Unit).map { MainIntent.WelcomeIntent }

    private fun loadIntsIntent(): Observable<MainIntent> =
        loadInts.clicks().map { MainIntent.LoadIntsIntent }


    override fun render(state: MainViewState) {
        Log.d("MviTesting", state.toString())
        when (state) {
            is MainViewState.WelcomeState -> renderWelcomeState(state.message)
            MainViewState.LoadingState -> renderLoadingState()
            is MainViewState.IntsDataState -> renderIntsDataState(state.data)
            MainViewState.Failure -> renderFailureState()
        }.exhaustive
    }


    private fun renderWelcomeState(message: String) {
        display.visibility = View.VISIBLE
        display.text = message
        loadInts.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun renderLoadingState() {
        display.visibility = View.GONE
        display.text = ""
        loadInts.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun renderIntsDataState(data: List<Int>) {
        display.visibility = View.VISIBLE
        display.text = data.toString()
        loadInts.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun renderFailureState() {
        display.visibility = View.VISIBLE
        display.text = "error"
        loadInts.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }


}


class UseCase {
    fun loadInts(): Observable<List<Int>> = Observable.timer(5, TimeUnit.SECONDS, Schedulers.io())
        .map { (1..10).toList() }
        .flatMap { Observable.just(it) }
}


val <T> T.exhaustive: T
    get() = this



