package com.uwantolearn.mvi.detail

import io.reactivex.Observable
import io.reactivex.ObservableTransformer

class DetailActivityProcessor(private val useCase: DetailUseCase) {

    private val initialActionProcessor =
        ObservableTransformer<DetailActivityAction.InitialAction, DetailActivityResult> { action ->
            action.flatMap {
                useCase.loadInitialMessage().toObservable()
                    .map<DetailActivityResult>(DetailActivityResult::InitialResult)
                    .onErrorReturn(DetailActivityResult::ErrorResult)
            }
        }

    private val eventOrOddActionProcessor =
        ObservableTransformer<DetailActivityAction.EvenOddAction, DetailActivityResult> { action ->
            action.flatMap {
                useCase.loadEvenOdd(it.v)
                    .map<DetailActivityResult>(DetailActivityResult::EvenOddResult)
                    .onErrorReturn(DetailActivityResult::ErrorResult)
                    .startWith(DetailActivityResult.LoadingResult)
            }
        }

    private val getLastStateActionProcessor =
        ObservableTransformer<DetailActivityAction.GetLastStateAction, DetailActivityResult> { action ->
            action.map { DetailActivityResult.GetLastStateResult }
        }


    var actionsProcessor = ObservableTransformer<DetailActivityAction, DetailActivityResult> { actions ->
        actions.publish { shared ->
            Observable.merge(
                shared.ofType(DetailActivityAction.InitialAction::class.java).compose(initialActionProcessor),
                shared.ofType(DetailActivityAction.EvenOddAction::class.java).compose(eventOrOddActionProcessor),
                shared.ofType(DetailActivityAction.GetLastStateAction::class.java).compose(getLastStateActionProcessor)
            )
        }
    }

}