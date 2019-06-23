package com.uwantolearn.mvi.detail

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

interface DetailUseCase {

    fun loadInitialMessage(): Single<String>
    fun loadEvenOdd(v: Int): Observable<Int>
}


class DetailUseCaseImpl : DetailUseCase {

    override fun loadInitialMessage(): Single<String> = Single.timer(2, TimeUnit.SECONDS, Schedulers.io())
        .map { "Detail Screen is ready" }


    private val listOfEvens = listOf(2, 4, 6, 8, 10)
    private val listOfOdds = listOf(1, 3, 5, 7, 9)

    override fun loadEvenOdd(v: Int): Observable<Int> = Observable.timer(5, TimeUnit.SECONDS)
        .map {
            if (v % 2 == 0) {
                listOfOdds.random()
            } else
                listOfEvens.random()
        }


}