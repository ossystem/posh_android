package ru.jufy.myposh.ui.favourites

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ru.jufy.myposh.R
import ru.jufy.myposh.presentation.favourites.FavouritesMvpView
import ru.jufy.myposh.presentation.favourites.FavouritesPresenter
import ru.jufy.myposh.presentation.global.RouterProvider
import ru.jufy.myposh.ui.fragments.ImageGridFragment
import java.util.*
import javax.inject.Inject


class FavoritesFragment : ImageGridFragment(), FavouritesMvpView {
    @Inject
    lateinit var presenter: FavouritesPresenter<FavouritesMvpView>

    private var cancelFab: FloatingActionButton? = null
    private var deleteFab: FloatingActionButton? = null
    private var selectAllFab: FloatingActionButton? = null

    private val isSelectionInProcess: Boolean
        get() = cancelFab!!.visibility == View.VISIBLE && selectAllFab!!.visibility == View.VISIBLE


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_favorites, container, false)

        presenter.onAttach(this)
        setUp(rootView)
        presenter.loadArtworks()
        return rootView
    }

    override fun setUp(view: View?) {
        cancelFab = rootView.findViewById(R.id.fab_cancel)
        cancelFab!!.setOnClickListener { view ->
            adapter.setSelectedAll(false)
            hideFab(cancelFab!!, selectAllFab!!)
        }

        deleteFab = rootView.findViewById(R.id.fab_delete)
        deleteFab!!.setOnClickListener { view ->
            if (!adapter.isAnySelected) {
                Toast.makeText(context, R.string.toast_delete_favorite,
                        Toast.LENGTH_SHORT).show()
            } else {
                presenter.deleteItems(adapter.selectedImages)
            }
        }

        selectAllFab = rootView.findViewById(R.id.fab_select_all)
        selectAllFab!!.setOnClickListener { adapter.setSelectedAll(true) }
        setupList()
        adapter.setSupportsDoubleClick(false)
        presenter.router = (parentFragment as RouterProvider).router
    }

    override fun getAllPoshiksAtPage(page: Int): List<Any> {


        return ArrayList()
    }

    override fun onDestroyView() {
        presenter.onDetach()
        super.onDestroyView()
    }

    private fun showFab(vararg fab: FloatingActionButton) {

        val list = ArrayList<Animator>()
        for (i in fab.indices) {
            fab[i].visibility = View.VISIBLE
            val scaleX = ObjectAnimator.ofFloat(fab[i], "scaleX", 0f, 1.2f, 1f)
            val scaleY = ObjectAnimator.ofFloat(fab[i], "scaleY", 0f, 1.2f, 1f)
            list.add(scaleX)
            list.add(scaleY)
        }

        val set = AnimatorSet()
        set.duration = 300
        set.playTogether(list)
        set.start()

    }


    private fun hideFab(vararg fab: FloatingActionButton) {

        val list = ArrayList<Animator>()
        for (i in fab.indices) {
            val scaleX = ObjectAnimator.ofFloat(fab[i], "scaleX", 1f, 1.2f, 0f)
            val scaleY = ObjectAnimator.ofFloat(fab[i], "scaleY", 1f, 1.2f, 0f)
            list.add(scaleX)
            list.add(scaleY)
        }

        val set = AnimatorSet()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {

            }

            override fun onAnimationEnd(animator: Animator) {
                for (i in fab.indices) {
                    fab[i].visibility = View.GONE
                }
            }

            override fun onAnimationCancel(animator: Animator) {

            }

            override fun onAnimationRepeat(animator: Animator) {

            }
        })
        set.duration = 300
        set.playTogether(list)
        set.start()

    }

    override fun setRefreshingState(refreshingState: Boolean) {

    }

    override fun setupList() {
        setupGrid()
        adapter.setClickListener(object : ImageClickListener() {
            override fun onSingleClick(view: View, position: Int) {
                if (isSelectionInProcess) {
                    adapter.setSelected(position, !adapter.isSelected(position))
                } else {
                    super.onSingleClick(view, position)
                }
            }

            override fun onDoubleClick(view: View, position: Int) {
                //not supported
            }

            override fun onLongClick(view: View, position: Int): Boolean {
                if (!isSelectionInProcess) {
                    showFab(selectAllFab!!, cancelFab!!)
                }
                adapter.setSelected(position, !adapter.isSelected(position))
                return true
            }
        })
        listener = presenter
    }

    override fun updateItems(items: MutableList<Any>) {
        adapter.setData(items)
    }

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }
}
