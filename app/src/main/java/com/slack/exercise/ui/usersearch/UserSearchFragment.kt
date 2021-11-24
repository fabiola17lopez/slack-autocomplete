package com.slack.exercise.ui.usersearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.slack.exercise.R
import com.slack.exercise.databinding.FragmentUserSearchBinding
import com.slack.exercise.model.UserSearchResult
import com.slack.exercise.model.UserSearchState
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

/**
 * Main fragment displaying and handling interactions with the view.
 * We use the MVP pattern and attach a Presenter that will be in charge of non view related operations.
 */
class UserSearchFragment : DaggerFragment(), UserSearchContract.View {

  @Inject
  internal lateinit var presenter: UserSearchPresenter

  private lateinit var userSearchBinding: FragmentUserSearchBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    super.onCreateView(inflater, container, savedInstanceState)
    userSearchBinding = FragmentUserSearchBinding.inflate(inflater, container, false)
    setHasOptionsMenu(true)
    return userSearchBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpToolbar()
    setUpList()
  }

  override fun onStart() {
    super.onStart()

    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()

    presenter.detach()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_user_search, menu)

    val searchView: SearchView = menu.findItem(R.id.search_menu_item).actionView as SearchView
    searchView.queryHint = getString(R.string.search_users_hint)
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String): Boolean {
        return true
      }

      override fun onQueryTextChange(newText: String): Boolean {
        presenter.onQueryTextChange(newText)
        return true
      }
    })
  }

  override fun onUserSearchResults(results: Set<UserSearchResult>) {
    userSearchBinding.apply {
      val searchAdapter = userSearchResultList.adapter as UserSearchAdapter
      searchAdapter.setResults(results)

      userSearchResultList.visibility = View.VISIBLE
      noResults.visibility = View.INVISIBLE
      errorText.visibility = View.INVISIBLE
    }
  }

  override fun onEmptySearchResults() {
    userSearchBinding.apply {
      userSearchResultList.visibility = View.INVISIBLE
      noResults.visibility = View.VISIBLE
      errorText.visibility = View.INVISIBLE
    }
  }

  override fun onUserSearchError(error: Throwable) {
    Timber.e(error, "Error searching users.")

    userSearchBinding.apply {
      userSearchResultList.visibility = View.INVISIBLE
      noResults.visibility = View.INVISIBLE
      errorText.visibility = View.VISIBLE
    }
  }

  private fun setUpToolbar() {
    val act = activity as UserSearchActivity
    act.setSupportActionBar(userSearchBinding.toolbar)
  }

  private fun setUpList() {
    with(userSearchBinding.userSearchResultList) {
      adapter = UserSearchAdapter()
      layoutManager = LinearLayoutManager(activity).apply {
        orientation = LinearLayoutManager.VERTICAL
      }
      setHasFixedSize(true)
    }
  }
}