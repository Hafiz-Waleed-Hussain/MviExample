package com.uwantolearn.mvi.order

import com.uwantolearn.mvi.base.MviAction
import com.uwantolearn.mvi.base.MviIntent
import com.uwantolearn.mvi.base.MviResult
import com.uwantolearn.mvi.base.MviViewState

sealed class OrderIntent : MviIntent
sealed class OrderAction : MviAction
sealed class OrderResult : MviResult
data class OrderState(val orderId: Int = 0) : MviViewState

//OrderIntent, OrderAction, OrderState, OrderResult
