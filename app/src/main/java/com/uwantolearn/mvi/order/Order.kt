package com.uwantolearn.mvi.order

import com.uwantolearn.mvi.R
import com.uwantolearn.mvi.base.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

//sealed class OrderIntent : MviIntent
//sealed class OrderAction : MviAction
//sealed class OrderResult : MviResult
//data class OrderState(val orderId: Int = 0) : MviViewState






























































































//class OrderActivity : MviBaseView<OrderIntent, OrderAction, OrderState, OrderResult>() {
//
//
//    override val viewModel: MviBaseViewModel<OrderIntent, OrderAction, OrderState, OrderResult>
//        get() = OrderViewModel()
//
//    override
//    val layoutId: Int
//        get() = R.layout.activity_home
//
//    override fun intents(): Observable<OrderIntent> {
//    }
//
//    override fun render(state: OrderState) {
//
//    }
//
//}

































































//class OrderViewModel() : MviBaseViewModel<OrderIntent, OrderAction, OrderState, OrderResult>(
//    OrderActionProcessor(),
//    OrderState()
//) {
//
//    override fun intentFilter(initialIntent: OrderIntent, newIntent: OrderIntent): OrderIntent {
//    }
//
//    override fun mapToActions(intent: OrderIntent): OrderAction {
//    }
//
//    override fun reduce(previousState: OrderState, result: OrderResult): OrderState {
//
//    }
//}



































































//class OrderActionProcessor : MviBaseActionProcessor<OrderAction, OrderResult>() {
//
//    override val processActions: ObservableTransformer<OrderAction, OrderResult>
//        get() = TODO()
//
//}