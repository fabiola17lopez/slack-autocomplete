package com.slack.exercise.model

sealed class UserSearchState {

    /*
     * Represents the state in which the API returns results
     */
    data class Results(val results: Set<UserSearchResult>): UserSearchState()

    /*
     * Represents the state in which there are no results
     */
    object NoResults: UserSearchState()
}