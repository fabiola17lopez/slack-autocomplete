package com.slack.exercise.model

/**
 * Models users returned by the API.
 */
data class UserSearchResult(
    val username: String,
    val displayName: String,
    val avatarUrl: String
)