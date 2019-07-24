package com.uwantolearn.mvi.mvi_presentation

import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
class MVIPresentationViewModelTest {
    @Mock
    lateinit var repo: MVIPresentationRepo
    lateinit var actionProcessor: MVIPresentationActionProcessor
    lateinit var viewModel: MVIPresentationViewModel
    lateinit var testObserver: TestObserver<HomeViewState>

    @Test
    fun `when LoadDataIntent successful should return data with`() {
        repo.loadData()
            .let(Mockito::`when`)
            .thenReturn(just(mockData))

        HomeIntent.LoadDataIntent
            .let(::just)
            .cast(HomeIntent::class.java)
            .let(viewModel::processIntents)

        testObserver.values()
            .let(::println)
        testObserver.assertValueAt(0, HomeViewState::inProgress)
        testObserver.assertValueAt(1) { !it.inProgress }
        testObserver.assertValueAt(1) { it.data == mockData }
    }

    @Test
    fun `when LoadDataIntent fail should return failure state`() {
        repo.loadData()
            .let(Mockito::`when`)
            .thenReturn(Observable.error(Exception()))

        HomeIntent.LoadDataIntent
            .let(::just)
            .cast(HomeIntent::class.java)
            .let(viewModel::processIntents)

        testObserver.values()
            .let(::println)

        testObserver.assertValueAt(0, HomeViewState::inProgress)
        testObserver.assertValueAt(1) { !it.inProgress }
        testObserver.assertValueAt(1, HomeViewState::isFail)

    }

    @Test
    fun `when RandomNumber click should return value`() {
        HomeIntent.GetRandomNumberIntent
            .let(::just)
            .cast(HomeIntent::class.java)
            .let(viewModel::processIntents)

        testObserver.values()
            .let(::println)

        testObserver.assertValueAt(0) { it.randomNumber != 0 }

    }

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
        viewModel = MVIPresentationViewModel(actionProcessor)

        testObserver = viewModel.state().test()
    }


}