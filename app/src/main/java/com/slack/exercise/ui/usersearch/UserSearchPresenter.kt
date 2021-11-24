package com.slack.exercise.ui.usersearch

import com.slack.exercise.dataprovider.UserSearchResultDataProvider
import com.slack.exercise.model.UserSearchState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val THROTTLE_TIME_MILLIS = 1500L
/**
 * Presenter responsible for reacting to user inputs and initiating search queries.
 */
class UserSearchPresenter @Inject constructor(
    private val userNameResultDataProvider: UserSearchResultDataProvider
) : UserSearchContract.Presenter {

  private var view: UserSearchContract.View? = null
  private val searchQuerySubject = PublishSubject.create<String>()
  private var searchQueryDisposable = Disposable.disposed()

  override fun attach(view: UserSearchContract.View) {
    this.view = view

    searchQueryDisposable = searchQuerySubject
        .throttleLatest(THROTTLE_TIME_MILLIS, TimeUnit.MILLISECONDS)
        .flatMapSingle { searchTerm ->
          if (searchTerm.isEmpty()) {
            Single.just(UserSearchState.Results(emptySet()))
          } else {
            userNameResultDataProvider.fetchUsers(searchTerm)
                .map { results ->
                    if (results.isEmpty()) {
                        UserSearchState.NoResults
                    } else {
                        UserSearchState.Results(results)
                    }
                }
          }
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            { state ->
                when (state) {
                    is UserSearchState.Results -> {
                        this@UserSearchPresenter.view?.onUserSearchResults(state.results)
                    }
                    UserSearchState.NoResults -> {
                        this@UserSearchPresenter.view?.onEmptySearchResults()
                    }
                }
            },
            { error -> this@UserSearchPresenter.view?.onUserSearchError(error) }
        )
  }

  override fun detach() {
    view = null
    searchQueryDisposable.dispose()
  }

  override fun onQueryTextChange(searchTerm: String) {
    searchQuerySubject.onNext(searchTerm)
  }
}