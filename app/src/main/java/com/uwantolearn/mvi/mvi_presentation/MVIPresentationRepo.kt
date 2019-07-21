package com.uwantolearn.mvi.mvi_presentation

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

interface MVIPresentationRepo {
    fun loadData(): Observable<List<String>>
}

class MVIPresentationRepoImpl : MVIPresentationRepo {

    override fun loadData(): Observable<List<String>> = Observable
        .timer(5, TimeUnit.SECONDS, Schedulers.io())
        .map {
            listOf(
                "GoJek Indonesia",
                "GoJekSingapore",
                "GoJekIndia",
                "GoJekThailand",
                "GoJekPhilippines",
                "GoJekVietnam"
            )
        }

}