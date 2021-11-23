package com.slack.exercise.dataprovider

import android.content.Context
import android.util.Log
import com.slack.exercise.R
import com.slack.exercise.api.SlackApi
import com.slack.exercise.dataprovider.trie.DenyListTrie
import com.slack.exercise.model.UserSearchResult
import io.reactivex.rxjava3.core.Single
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [UserSearchResultDataProvider].
 */
@Singleton
class UserSearchResultDataProviderImpl @Inject constructor(
    private val slackApi: SlackApi,
    private val context: Context
) : UserSearchResultDataProvider {
    private val denyList = DenyListTrie()

    init {
        loadDenyList()
    }

    /**
     * loads the values from our raw text file into a DenyListTrie
     */
    private fun loadDenyList() {
        val inputStream = context.resources.openRawResource(R.raw.denylist)
        val reader = BufferedReader(InputStreamReader(inputStream))

        var currentString: String? = null
        while (true) {
            try {
                currentString = reader.readLine()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (currentString == null) {
                // we've reached the end of the file
                break
            } else {
                // add the value to our deny list
                denyList.insert(currentString)
            }
        }
    }

    /**
     * Returns a [Single] emitting a set of [UserSearchResult].
     */
    override fun fetchUsers(searchTerm: String): Single<Set<UserSearchResult>> {
        return checkDenyList(searchTerm) { term ->
            slackApi.searchUsers(term)
                .map {
                    it.map { user ->
                        UserSearchResult(user.username, user.displayName, user.avatarUrl)
                    }.toSet()
                }
        }
    }

    /**
     * Checks [DenyListTrie] for search term.
     *
     * Returns an empty set if a prefix exists in the denylist, otherwise returns the result of the
     * provided block.
     */
    private fun checkDenyList(
        searchTerm: String,
        block: (String) -> Single<Set<UserSearchResult>>
    ): Single<Set<UserSearchResult>> {
        return if (denyList.containsPrefixForTerm(searchTerm)) {
            Single.just(emptySet())
        } else {
            block(searchTerm)
        }
    }
}
