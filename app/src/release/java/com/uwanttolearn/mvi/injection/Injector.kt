package com.uwantolearn.mvi.injection

import com.uwanttolearn.mvi.injection.MVIPresentationRepoImpl


object Injector {

    fun getMVIPresentationRepo() = MVIPresentationRepoImpl()
}