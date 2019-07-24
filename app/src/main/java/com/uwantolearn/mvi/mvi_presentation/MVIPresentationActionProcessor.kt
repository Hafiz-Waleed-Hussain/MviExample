package com.uwantolearn.mvi.mvi_presentation

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random


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