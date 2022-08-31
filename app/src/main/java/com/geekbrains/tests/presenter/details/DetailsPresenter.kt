package com.geekbrains.tests.presenter.details

import com.geekbrains.tests.view.ViewContract
import com.geekbrains.tests.view.details.ViewDetailsContract

internal class DetailsPresenter internal constructor(
    private var count: Int = 0
) : PresenterDetailsContract {

    private var _viewContract: ViewDetailsContract? = null
    val viewContract: ViewContract? get() = _viewContract

    override fun onAttach(viewContract: ViewContract) {
        this._viewContract = viewContract as ViewDetailsContract
    }

    override fun onDetach() {
        if (this._viewContract != null) {
            this._viewContract = null
        }
    }

    override fun setCounter(count: Int) {
        this.count = count
    }

    override fun onIncrement() {
        count++
        _viewContract?.setCount(count)
    }

    override fun onDecrement() {
        count--
        _viewContract?.setCount(count)
    }
}
