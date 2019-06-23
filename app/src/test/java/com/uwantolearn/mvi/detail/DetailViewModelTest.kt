package com.uwantolearn.mvi.detail

import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class DetailViewModelTest {

    private lateinit var viewModel: DetailViewModel
    private lateinit var testObserver: TestObserver<DetailViewState>

    @Mock
    lateinit var useCase: DetailUseCase


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }


        viewModel = DetailViewModel(useCase)

        testObserver = viewModel.state().test()
    }


    @Test
    fun `when initial Intent call`() {

        viewModel.processIntents(Observable.just(DetailActivityIntent.InitialIntent))

        testObserver.assertValueAt(0) { it is DetailViewState.Initial }
    }

    @Test
    fun `when EvenOdd Intent call`() {

        Mockito.`when`(useCase.loadEvenOdd(1)).thenReturn(Observable.just(3))

        viewModel.processIntents(
            Observable
                .just(
                    DetailActivityIntent.EvenOdd(1)
                )
        )

        testObserver.values().forEach(::println)

        testObserver.assertValueAt(0) { it is DetailViewState.Loading }
        testObserver.assertValueAt(1) {it is DetailViewState.EventOrOdd}
    }


}