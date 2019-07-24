package com.uwanttolearn.mvi.injection

import com.uwantolearn.mvi.mvi_presentation.MVIPresentationRepo
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MVIPresentationRepoImpl : MVIPresentationRepo {

    override fun loadData(): Observable<List<String>> = Observable.timer(
        5,
        TimeUnit.SECONDS,
        Schedulers.io()
    )
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