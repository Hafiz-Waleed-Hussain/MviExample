package com.uwantolearn.mvi.mvi_presentation

import io.reactivex.Observable.error
import io.reactivex.Observable.just
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MVIPresentationActionProcessorTest {

    @Mock
    lateinit var repo: MVIPresentationRepo
    lateinit var actionProcessor: MVIPresentationActionProcessor
    private val mockData = listOf(
        "GoJek Indonesia",
        "GoJekSingapore",
        "GoJekIndia",
        "GoJekThailand",
        "GoJekPhilippines",
        "GoJekVietnam"
    )

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        actionProcessor = MVIPresentationActionProcessor(repo)
    }

    @Test
    fun `when LoadDataAction success should return DataResult`() {
        repo.loadData()
            .let(Mockito::`when`)
            .thenReturn(mockData.let(::just))

        val testObserver = HomeActivityAction.LoadDataAction.let(::just)
            .compose(actionProcessor.processActions)
            .test()

        testObserver.values()
            .let(::println)
        testObserver.assertValueAt(0, HomeActivityResult.LoadingResult)
        testObserver.assertValueAt(1) { it == HomeActivityResult.DataResult(mockData) }
    }


    @Test
    fun `when LoadDataAction failed should return FailedResult`() {
        repo.loadData()
            .let(Mockito::`when`)
            .thenReturn(error(Exception()))

        val testObserver = HomeActivityAction.LoadDataAction.let(::just)
            .compose(actionProcessor.processActions)
            .test()

        testObserver.values()
            .let(::println)
        testObserver.assertValueAt(0, HomeActivityResult.LoadingResult)
        testObserver.assertValueAt(1, HomeActivityResult.FailureResult)
    }
}