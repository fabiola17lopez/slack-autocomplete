package com.slack.exercise.ui.usersearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.slack.exercise.R
import com.slack.exercise.model.UserSearchResult
import kotlinx.android.synthetic.main.item_user_search.view.*

private const val ROUNDED_CORNER_RADIUS = 8

/**
 * Adapter for the list of [UserSearchResult].
 */
class UserSearchAdapter : RecyclerView.Adapter<UserSearchAdapter.UserSearchViewHolder>() {
  private var userSearchResults: List<UserSearchResult> = emptyList()

  fun setResults(results: Set<UserSearchResult>) {
    userSearchResults = results.toList()
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSearchViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_search, parent, false)
    return UserSearchViewHolder(view)
  }

  override fun getItemCount(): Int {
    return userSearchResults.size
  }

  override fun onBindViewHolder(holder: UserSearchViewHolder, position: Int) {
    val result = userSearchResults[position]
    holder.username.text = result.username
    holder.displayName.text = result.displayName

    Glide.with(holder.itemView.context)
      .load(result.avatarUrl)
      .fitCenter()
      .transform(RoundedCorners(ROUNDED_CORNER_RADIUS))
      .into(holder.avatar)
  }

  class UserSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val username: TextView = itemView.username
    val displayName: TextView = itemView.displayName
    val avatar: ImageView = itemView.avatar
  }
}