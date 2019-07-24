package com.uwanttolearn.mvi.injection

import com.uwantolearn.mvi.mvi_presentation.MVIPresentationRepo
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class FakeMVIPresentationRepoImpl : MVIPresentationRepo {

    override fun loadData(): Observable<List<String>> = Observable.timer(3, TimeUnit.SECONDS)
        .map {
            listOf(
                "Fake GoJek Indonesia",
                "Fake GoJekSingapore",
                "Fake GoJekIndia",
                "Fake GoJekThailand",
                "Fake GoJekPhilippines",
                "Fake GoJekVietnam"
            )
        }

}