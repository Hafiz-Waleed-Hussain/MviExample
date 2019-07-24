package com.uwantolearn.mvi.mvi_presentation

import io.reactivex.Observable

interface MVIPresentationRepo {
    fun loadData(): Observable<List<String>>
}

