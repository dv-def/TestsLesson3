package com.geekbrains.tests

import com.geekbrains.tests.presenter.details.DetailsPresenter
import com.geekbrains.tests.view.details.ViewDetailsContract
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DetailsPresenterTest {
    private lateinit var presenter: DetailsPresenter

    @Mock
    private lateinit var viewContract: ViewDetailsContract

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        presenter = DetailsPresenter()
    }

    @Test
    fun onAttachTest() {
        presenter.onAttach(viewContract)
        assertNotNull(presenter.viewContract)
        presenter.onDetach()
    }

    @Test
    fun onDetach() {
        presenter.onAttach(viewContract)
        assertNotNull(presenter.viewContract)
        presenter.onDetach()
        assertNull(presenter.viewContract)
    }

    @Test
    fun increment() {
        presenter.onAttach(viewContract)

        val count = 5
        presenter.setCounter(count)
        presenter.onIncrement()

        verify(viewContract, times(1)).setCount(6)

        presenter.onDetach()
    }

    @Test
    fun decrement() {
        presenter.onAttach(viewContract)

        val count = 5
        presenter.setCounter(count)

        presenter.onDecrement()
        verify(viewContract, times(1)).setCount(4)

        presenter.onDetach()
    }
}