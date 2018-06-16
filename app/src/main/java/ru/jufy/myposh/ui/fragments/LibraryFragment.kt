package ru.jufy.myposh.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_library.*
import ru.jufy.myposh.R
import ru.jufy.myposh.presentation.global.RouterProvider
import ru.jufy.myposh.presentation.library.LibraryMvpView
import ru.jufy.myposh.presentation.library.LibraryPresenter
import ru.jufy.myposh.ui.activities.IntentDispatcherActivity
import java.util.*
import javax.inject.Inject


class LibraryFragment : ImageGridFragment(), LibraryMvpView {

    internal var currentListType = SHOW_PURCHASED
    @Inject
    lateinit var presenter: LibraryPresenter<LibraryMvpView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_library, container, false)

        presenter.onAttach(this)
        currentListType = SHOW_PURCHASED
        setUp(rootView)
        return rootView
    }

    override fun setUp(view: View?) {
        setupList()
        presenter.router = (parentFragment as RouterProvider).router
        presenter.loadData()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun getAllPoshiksAtPage(page: Int): List<Any>? {
        return null
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context !is IntentDispatcherActivity) {
            throw IllegalArgumentException()
        }
    }

    override fun updateBalance(balance: Long) {
        balanceView?.text = String.format(Locale.getDefault(), getString(R.string.balance_title), balance)
    }

    override fun updateItems(items: MutableList<Any>) {
        adapter.setData(items)
    }

    override fun setRefreshingState(refreshingState: Boolean) {

    }

    override fun setupList() {
        setupGrid()
        adapter.setClickListener(ImageClickListener())
        listener = presenter
    }

    companion object {
        private val SHOW_PURCHASED = 0

        fun newInstance(): LibraryFragment {
            return LibraryFragment()
        }
    }
}
