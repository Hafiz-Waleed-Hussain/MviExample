package com.uwantolearn.mvi.mvi_presentation

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlin.random.Random

class MVIPresentationViewModel(repo: MVIPresentationRepo) : ViewModel() {

    private val actionProcessor = MVIPresentationActionProcessor(repo)
    private val intentsSubject = PublishSubject.create<HomeIntent>()
    private val states = PublishSubject.create<HomeViewState>()

    init {
        intentsSubject
            .scan(::intentFilter)
            .map(::mapToActions)
            .compose(actionProcessor.processActions)
            .scan(HomeViewState(), ::reduce)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(states)
    }


    fun processIntents(intents: Observable<HomeIntent>): Disposable = intents
        .subscribe(intentsSubject::onNext)

    fun state(): Observable<HomeViewState> = states.hide()

    override fun onCleared() {
        intentsSubject.onComplete()
        states.onComplete()
        super.onCleared()

    }

    private fun intentFilter(initialIntent: HomeIntent, newIntent: HomeIntent): HomeIntent =
        if (newIntent is HomeIntent.LoadDataIntent)
            HomeIntent.GetLastStateIntent
        else
            newIntent

    private fun mapToActions(intent: HomeIntent): HomeActivityAction = when (intent) {
        HomeIntent.LoadDataIntent, HomeIntent.RefreshIntent -> HomeActivityAction.LoadDataAction
        HomeIntent.GetRandomNumberIntent -> HomeActivityAction.GetRandomNumberAction
        HomeIntent.GetLastStateIntent -> HomeActivityAction.GetLastStateAction
    }

    private fun reduce(previousState: HomeViewState, result: HomeActivityResult): HomeViewState =
        when (result) {
            is HomeActivityResult.DataResult -> previousState.copy(data = result.data, inProgress = false)
            HomeActivityResult.FailureResult -> previousState.copy(isFail = true,inProgress = false)
            HomeActivityResult.LoadingResult -> previousState.copy(inProgress = true)
            is HomeActivityResult.RandomNumber -> previousState.copy(randomNumber = result.randomNumber)
            HomeActivityResult.GetLastState -> previousState
        }
}

sealed class HomeActivityAction {
    object LoadDataAction : HomeActivityAction()
    object GetRandomNumberAction : HomeActivityAction()
    object GetLastStateAction : HomeActivityAction()
}

sealed class HomeActivityResult {
    data class DataResult(val data: List<String>) : HomeActivityResult()
    data class RandomNumber(val randomNumber: Int) : HomeActivityResult()
    object GetLastState : HomeActivityResult()
    object FailureResult : HomeActivityResult()
    object LoadingResult : HomeActivityResult()
}

class MVIPresentationActionProcessor(private val repo: MVIPresentationRepo) {


    private val loadDataActionProcessor =
        ObservableTransformer<HomeActivityAction.LoadDataAction, HomeActivityResult> { action ->
            action.flatMap {
                repo.loadData()
                    .map(HomeActivityResult::DataResult)
                    .cast(HomeActivityResult::class.java)
                    .onErrorReturn { HomeActivityResult.FailureResult }
                    .subscribeOn(Schedulers.io())
                    .startWith(HomeActivityResult.LoadingResult)
            }
        }

    private val randomNumberActionProcessor =
        ObservableTransformer<HomeActivityAction.GetRandomNumberAction, HomeActivityResult> { action ->
            action.map { HomeActivityResult.RandomNumber(Random.nextInt()) }
        }

    private val getLastStateActionProcessor =
        ObservableTransformer<HomeActivityAction.GetLastStateAction, HomeActivityResult> { action ->
            action.map { HomeActivityResult.GetLastState }
        }


    val processActions = ObservableTransformer<HomeActivityAction, HomeActivityResult> { action ->
        action.publish { actionSource ->
            Observable.merge(
                actionSource.ofType(HomeActivityAction.LoadDataAction::class.java)
                    .compose(loadDataActionProcessor),
                actionSource.ofType(HomeActivityAction.GetRandomNumberAction::class.java)
                    .compose(randomNumberActionProcessor),
                actionSource.ofType(HomeActivityAction.GetLastStateAction::class.java)
                    .compose(getLastStateActionProcessor)
            )
        }
    }
}