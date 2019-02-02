package com.kshitijchauhan.haroldadmin.moviedb.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.jakewharton.rxbinding2.internal.Notification
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxrelay2.PublishRelay
import com.kshitijchauhan.haroldadmin.moviedb.R
import com.kshitijchauhan.haroldadmin.moviedb.repository.collections.CollectionType
import com.kshitijchauhan.haroldadmin.moviedb.ui.BaseFragment
import com.kshitijchauhan.haroldadmin.moviedb.ui.UIState
import com.kshitijchauhan.haroldadmin.moviedb.ui.common.BackPressListener
import com.kshitijchauhan.haroldadmin.moviedb.ui.common.EpoxyCallbacks
import com.kshitijchauhan.haroldadmin.moviedb.ui.common.model.LoadingTask
import com.kshitijchauhan.haroldadmin.moviedb.utils.EqualSpaceGridItemDecoration
import com.kshitijchauhan.haroldadmin.moviedb.utils.extensions.getNumberOfColumns
import com.kshitijchauhan.haroldadmin.moviedb.utils.extensions.log
import com.mikepenz.itemanimators.AlphaInAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.view_searchbox.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class HomeFragment : BaseFragment(), BackPressListener {

    private val TAG_GET_POPULAR_MOVIES = "get-popular-movies"
    private val TAG_GET_TOP_RATED_MOVIES = "get-top-rated-movies"
    private val TASK_GET_SEARCH_RESULTS = "get-search-results"

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val homeViewModel: HomeViewModel by viewModel()

    private val callbacks = object : EpoxyCallbacks {
        override fun onMovieItemClicked(id: Int, transitionName: String, sharedView: View?) {
            mainViewModel.updateStateTo(UIState.DetailsScreenState(id, transitionName, sharedView))
        }
    }

    private val homeEpoxyController = HomeEpoxyController(callbacks)

    private val onDestroyView: PublishRelay<Any> = PublishRelay.create()

    override val associatedUIState: UIState = UIState.HomeScreenState

    override fun notifyBottomNavManager() {
        mainViewModel.updateBottomNavManagerState(this.associatedUIState)
    }

    override fun updateToolbarTitle() {
        mainViewModel.updateToolbarTitle("Home")
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeEpoxyController.setData(null, null, null)
        homeViewModel.apply {

            if (popularMovies.value == null) {
                mainViewModel.addLoadingTask(LoadingTask(TAG_GET_POPULAR_MOVIES, this@HomeFragment.viewLifecycleOwner))
                getPopularMovies()
            }

            if (topRatedMovies.value == null) {
                mainViewModel.addLoadingTask(
                    LoadingTask(
                        TAG_GET_TOP_RATED_MOVIES,
                        this@HomeFragment.viewLifecycleOwner
                    )
                )
                getTopRatedMovies()
            }

            popularMovies.observe(viewLifecycleOwner, Observer {
                mainViewModel.completeLoadingTask(TAG_GET_POPULAR_MOVIES, viewLifecycleOwner)
                updateEpoxyController()
            })

            topRatedMovies.observe(viewLifecycleOwner, Observer {
                mainViewModel.completeLoadingTask(TAG_GET_TOP_RATED_MOVIES, viewLifecycleOwner)
                updateEpoxyController()
            })

            searchResults.observe(viewLifecycleOwner, Observer {
                mainViewModel.completeLoadingTask(TASK_GET_SEARCH_RESULTS, viewLifecycleOwner)
                updateEpoxyController()
            })

            message.observe(viewLifecycleOwner, Observer { message ->
                mainViewModel.showSnackbar(message)
            })

            forceRefreshCollection(CollectionType.Popular)
            forceRefreshCollection(CollectionType.TopRated)
        }

        mainViewModel.updateToolbarTitle(getString(R.string.app_name))
        mainViewModel.setBackPressListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postponeEnterTransition()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateToolbarTitle()
        rvHome.apply {
            val columns = resources.getDimension(R.dimen.movie_grid_poster_width).getNumberOfColumns(context!!)
            val space = resources.getDimension(R.dimen.movie_grid_item_space)
            layoutManager = GridLayoutManager(context, columns)
            itemAnimator = AlphaInAnimator()
            addItemDecoration(EqualSpaceGridItemDecoration(space.roundToInt()))
            setController(homeEpoxyController)
        }
        (view.parent as ViewGroup).doOnPreDraw {
            startPostponedEnterTransition()
        }
        setupSearchBox()
    }

    private fun setupSearchBox() {
        RxTextView.textChangeEvents(searchBox.etSearchBox)
            .debounce(300, TimeUnit.MILLISECONDS)
            .map { event -> event.text().toString() }
            .takeUntil(onDestroyView)
            .doOnNext { query ->
                homeViewModel.getSearchResultsForQuery(query)
                if (query.length > 2)
                    mainViewModel.addLoadingTask(LoadingTask(TASK_GET_SEARCH_RESULTS, viewLifecycleOwner))
            }
            .subscribe()
    }

    private fun updateEpoxyController() {
        with(homeViewModel) {
            homeEpoxyController.setData(
                popularMovies.value,
                topRatedMovies.value,
                searchResults.value
            )
        }
    }

    override fun onBackPressed(): Boolean {
        return with(homeViewModel) {
            searchResults.value?.let {
                clearSearchResults()
                searchBox.etSearchBox.setText("")
                this@HomeFragment.view?.requestFocus()
                false
            } ?: true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyView.accept(Notification.INSTANCE)
        mainViewModel.setBackPressListener(null)
    }
}
