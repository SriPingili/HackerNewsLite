package com.android.hackernewslite.play.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.extensions.relativeDateFormat
import com.android.hackernewslite.play.model.HackerStory
import com.android.hackernewslite.play.util.Constants
import com.android.hackernewslite.play.util.SharePreferenceUtil
import kotlinx.android.synthetic.main.item_article_preview.view.*
import java.net.URL
import java.util.*

/*
Adapter class for the recycler view
* */
class HackerFeedAdapter : RecyclerView.Adapter<HackerFeedAdapter.ArticleViewHolder>() {
    private var onItemClickListener: ((HackerStory) -> Unit)? = null
    private var onSaveImageClickListener: ((HackerStory) -> Unit)? = null
    val TAG = HackerFeedAdapter::class.java.simpleName
    var fullList: MutableList<HackerStory> = mutableListOf()

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<HackerStory>() {
        override fun areItemsTheSame(oldItem: HackerStory, newItem: HackerStory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HackerStory, newItem: HackerStory): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.itemView.apply {
            scoreTextViewId.text = article.score.toString()
            titleTextViewId.text = article.title
            var url = article.url
            try {
                url = URL(url).host
            } catch (e: Exception) {
                Log.v(TAG, String.format(Constants.ERROR_MESSAGE, e.message))
            }
            urlTextViewId.text = url
            authorTextViewId.text = "by ${article.by}"

            val count = article.kids?.size ?: 0
            commentsCountTextViewId.text = count.toString()
            createdAtTextViewId.text =
                article.time?.times(1000)?.let { Date(it).relativeDateFormat(context) }
            setImageBackground(article, clickToSaveImageViewId, context)

            clickToSaveImageViewId.setOnClickListener { v ->
                onSaveImageClickListener?.let {
                    article.isImageSaved = !SharePreferenceUtil.getSavedStatus(article.id, context)
                    SharePreferenceUtil.setSavedStatus(article.id, article.isImageSaved, context)
                    setImageBackground(article, clickToSaveImageViewId, context)
                    it(article)
                }
            }

            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    private fun setImageBackground(article: HackerStory, view: ImageView, context: Context) {
        if (SharePreferenceUtil.getSavedStatus(article.id, context)) {
            view.setImageResource(R.drawable.ic_baseline_star_selected_24)
        } else {
            view.setImageResource(R.drawable.ic_baseline_star_not_selected_24)
        }
    }

    /*
    on click listener for the recycler view row item
    * */
    fun setOnItemClickListener(listener: (HackerStory) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnImageClickListener(listener: (HackerStory) -> Unit) {
        onSaveImageClickListener = listener
    }


    /*
    submits the hacker news response to the differ util, also saves
    the response to fullList (used by search)
     */
    fun submitList(stores: List<HackerStory>) {
        differ.submitList(stores)
        fullList.clear()
        fullList.addAll(stores.toMutableList())
    }

    /*
    helper method that filters through the data as user types
    in the search view
    * */
    fun filter(query: String?) {
        if (query == null || query.isEmpty()) {
            differ.submitList(fullList)
        } else {
            val lowercaseQuery = query.toLowerCase()
            differ.submitList(
                (
                        fullList.filter {
                            it.title?.toLowerCase()?.contains(lowercaseQuery)!!
                        })
            )
        }
    }
}