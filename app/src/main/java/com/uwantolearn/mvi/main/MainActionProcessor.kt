package com.uwantolearn.mvi.main

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers

class MainActionProcessor(
    private val useCase: UseCase = UseCase()
) {

    private val welcomeProcessor = ObservableTransformer<MainAction.WelcomeAction, MainResult> { action ->
        action.map { MainResult.WelcomeMessage("Welcome to Mvi") }
    }

    private val loadsIntsProcessor = ObservableTransformer<MainAction.LoadIntsAction, MainResult> { action ->
        action.flatMap { action ->
            useCase.loadInts()
                .map<MainResult> { MainResult.LoadIntsResult(it) }
                .onErrorReturn { MainResult.Failure }
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(MainResult.Loading)
        }
    }


    val actionProcessor = ObservableTransformer<MainAction, MainResult> { action: Observable<MainAction> ->
        action.publish { shared ->
            Observable.merge<MainResult>(
                shared.ofType(MainAction.WelcomeAction::class.java).compose(welcomeProcessor),
                shared.ofType(MainAction.LoadIntsAction::class.java).compose(loadsIntsProcessor),
                shared.ofType(MainAction.GetLastStateAction::class.java).map { MainResult.LastState }

            )
        }
    }
}