package com.uwantolearn.mvi.injection

import com.uwanttolearn.mvi.injection.FakeMVIPresentationRepoImpl


object Injector {

    fun getMVIPresentationRepo() = FakeMVIPresentationRepoImpl()
}