package ru.jufy.myposh.ui.store

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.jufy.mgtshr.extensions.showKeyboardAndFocus
import com.ogaclejapan.arclayout.ArcLayout
import kotlinx.android.synthetic.main.fragment_market.*
import ru.jufy.myposh.R
import ru.jufy.myposh.entity.Category
import ru.jufy.myposh.presentation.global.RouterProvider
import ru.jufy.myposh.presentation.store.StoreMvpView
import ru.jufy.myposh.presentation.store.StorePresenter
import ru.jufy.myposh.ui.fragments.ImageGridFragment
import ru.jufy.myposh.ui.utils.AnimatorUtils
import java.util.*
import javax.inject.Inject


class MarketFragment : ImageGridFragment(), StoreMvpView {

    @Inject
    internal lateinit var presenter: StorePresenter<StoreMvpView>

    internal lateinit var shadowBg: View
    internal lateinit var arcLayout: ArcLayout
    private var categoriesView: RecyclerView? = null
    private var progressBarArc: ProgressBar? = null

    override fun getAllPoshiksAtPage(page: Int): List<Any>? {
        return null
    }

    override fun onResume() {
        super.onResume()
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_market, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arcLayout = rootView.findViewById(R.id.search_menu)
        shadowBg = rootView.findViewById(R.id.shadow_bg)
        setUp(rootView)
    }

    override fun onStart() {
        super.onStart()
        presenter.onAttach(this)
        presenter.loadArtworks()
    }


    override fun setUp(view: View?) {
        fab_cancel?.setOnClickListener {
            presenter.clearFilterClicked()
            hideMenu()
        }
        fab_search.setOnClickListener { _ -> onFabSearchClick(fab_search) }
        fab_category.setOnClickListener { onFabCategoryClick() }
        fab_hashtag.setOnClickListener { presenter.getTags() }
        fab_artist?.setOnClickListener { presenter.getArtists() }
        shadowBg.setOnClickListener { hideMenu() }

        setupList()

        presenter.router = (parentFragment as RouterProvider).router
    }

    private fun onFabCategoryClick() {
        val layout = rootView.findViewById<FrameLayout>(R.id.tag_and_category)
        layout.removeAllViews()
        categoriesView = RecyclerView(context)
        categoriesView!!.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        val marginLayoutParams = ViewGroup.MarginLayoutParams(categoriesView!!.layoutParams)
        marginLayoutParams.setMargins(50, 0, 50, 0)
        categoriesView!!.layoutParams = marginLayoutParams
        categoriesView!!.setBackgroundColor(-0x20000001)
        categoriesView!!.layoutManager = LinearLayoutManager(context)

        layout.addView(categoriesView)
        presenter.getCategories()
    }

    override fun updateArtists(mutablelist: MutableList<String>) {
        showAutoComplete(mutablelist, { presenter.loadArtist(it) }, R.string.artist)
    }

    override fun updateTags(mutableList: MutableList<String>) {
        showAutoComplete(mutableList, { presenter.loadArtWorksByTag(it) }, R.string.hint_tag)
    }

    private fun showAutoComplete(mutableList: MutableList<String>,
                                 selectedListener: (String) -> Unit = {},
                                 @StringRes hint: Int) {
        val layout = rootView.findViewById<FrameLayout>(R.id.tag_and_category)
        layout.removeAllViews()
        val inputTag = AutoCompleteTextView(context)
        inputTag.background = ColorDrawable(-0x1)
        inputTag.setTextColor(ResourcesCompat.getColor(resources, R.color.primary_dark, null))
        inputTag.setHint(hint)
        inputTag.gravity = Gravity.CENTER_HORIZONTAL
        inputTag.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (hasFocus) {
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
            } else {
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
        inputTag.setSingleLine()
        inputTag.maxLines = 1
        inputTag.inputType = EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE

        inputTag.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                onFabSearchClick(fab_search)
                selectedListener(v.text.trim().toString())
                true
            } else {
                false
            }
        }

        val adapterTag = ArrayAdapter(context!!,
                R.layout.tag_search,
                mutableList)
        inputTag.setAdapter(adapterTag)
        inputTag.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            onFabSearchClick(fab_search)
            selectedListener(adapterTag.getItem(position)!!)
        }

        layout.addView(inputTag)
        inputTag.showKeyboardAndFocus(context)
    }

    override fun updateCategories(mutableList: MutableList<Category>) {
        if (categoriesView != null) {
            val adapter = CategoriesAdapter(context!!, mutableList)
            categoriesView!!.adapter = adapter
        }
    }

    override fun toggleArcLayoutProgressVisibility(isVisible: Boolean) {
        val layout = rootView.findViewById<FrameLayout>(R.id.tag_and_category)
        if (isVisible) {
            progressBarArc = ProgressBar(context)
            val layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL
            progressBarArc!!.layoutParams = layoutParams

            val marginLayoutParams = ViewGroup.MarginLayoutParams(progressBarArc!!.layoutParams)
            marginLayoutParams.setMargins(0, 50, 0, 0)
            progressBarArc!!.layoutParams = marginLayoutParams
            layout.addView(progressBarArc)
        } else {
            layout.removeView(progressBarArc)
        }
    }

    override fun setRefreshingState(refreshingState: Boolean) {

    }

    override fun setupList() {
        setupGrid()
        adapter.setClickListener(ImageClickListener())
        listener = presenter
    }

    override fun updateItems(items: MutableList<Any>) {
        adapter.setData(items)
    }

    internal inner class CategoriesAdapter(var context: Context, var items: List<Category>) : RecyclerView.Adapter<CategoriesAdapter.CategoryHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesAdapter.CategoryHolder {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
            return CategoryHolder(v)
        }

        override fun onBindViewHolder(holder: CategoriesAdapter.CategoryHolder, position: Int) {
            holder.item.text = items[position].name
            holder.item.setOnClickListener {
                onFabSearchClick(fab_search)
                presenter.loadCategory(items[position])
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var item: TextView

            init {
                item = itemView.findViewById(R.id.list_item_text)
            }
        }
    }

    //--------------- menu animation methods --------------------

    private fun onFabSearchClick(v: View) {
        if (v.isSelected) {
            hideMenu()
        } else {
            showMenu()
        }
        v.isSelected = !v.isSelected
    }

    private fun showMenu() {
        //Buttons
        arcLayout.visibility = View.VISIBLE
        val animList = ArrayList<Animator>()

        var i = 0
        val len = arcLayout.childCount
        while (i < len) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)))
            i++
        }

        val animSet = AnimatorSet()
        animSet.duration = 400
        animSet.interpolator = OvershootInterpolator()
        animSet.playTogether(animList)

        setShadowOn()

        animSet.start()
    }

    private fun setShadowOn() {
        setShadow(0f, 1.0f, View.VISIBLE)
    }

    private fun setShadow(fromAlpha: Float, toAlpha: Float, visible: Int) {
        val animBg = AlphaAnimation(fromAlpha, toAlpha)
        shadowBg.visibility = visible
        animBg.duration = 400
        shadowBg.alpha = 1f
        shadowBg.startAnimation(animBg)
    }

    private fun hideMenu() {
        //Buttons
        val animList = ArrayList<Animator>()

        for (i in arcLayout.childCount - 1 downTo 0) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)))
        }

        val animSet = AnimatorSet()
        animSet.duration = 400
        animSet.interpolator = AnticipateInterpolator()
        animSet.playTogether(animList)
        animSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                arcLayout.visibility = View.INVISIBLE
            }
        })

        setShadowOff()

        animSet.start()

        val layout = rootView.findViewById<View>(R.id.tag_and_category) as FrameLayout
        layout.removeAllViews()
    }

    private fun setShadowOff() {
        setShadow(1.0f, 0f, View.GONE)
    }

    private fun createShowItemAnimator(item: View): Animator {

        val dx = fab_search.x - item.x
        val dy = fab_search.y - item.y

        item.rotation = 0f
        item.translationX = dx
        item.translationY = dy
        item.scaleX = 0f
        item.scaleY = 0f
        val anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f),
                AnimatorUtils.scaleX(0f, 1f),
                AnimatorUtils.scaleY(0f, 1f)
        )

        return ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f),
                AnimatorUtils.scaleX(0f, 1f),
                AnimatorUtils.scaleY(0f, 1f)
        )
    }

    private fun createHideItemAnimator(item: View): Animator {
        val dx = fab_search.x - item.x
        val dy = fab_search.y - item.y

        val anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy),
                AnimatorUtils.scaleX(1f, 0f),
                AnimatorUtils.scaleY(1f, 0f)
        )

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                item.translationX = 0f
                item.translationY = 0f
            }
        })

        return anim
    }

    companion object {
        fun newInstance(): MarketFragment {
            return MarketFragment()
        }
    }
}
