package com.android.hackernewslite.play.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.model.HackerStory
import kotlinx.android.synthetic.main.item_article_preview.view.*
import java.net.URL

class HackerFeedAdapter : RecyclerView.Adapter<HackerFeedAdapter.ArticleViewHolder>() {
    private var onItemClickListener: ((HackerStory) -> Unit)? = null
    private var onSaveImageClickListener: ((HackerStory) -> Unit)? = null
    var onBindComplete = true

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    var fullList : MutableList<HackerStory> = mutableListOf()

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
        onBindComplete = false

        val article = differ.currentList[position]

        holder.itemView.apply {
            scoreTextViewId.text = article.score.toString()
            titleTextViewId.text = article.title

            var url = article.url

            try {
                url = URL(url).host
            } catch (e: Exception) {

            }


            urlTextViewId.text = url
            authorTextViewId.text = "by ${article.by}"
            commentsCountTextViewId.text = article.kids?.size.toString()
            createdAtTextViewId.text = "Yesterday"
            setImageBackground(article, clickToSaveImageViewId)

            clickToSaveImageViewId.setOnClickListener { v ->
                onSaveImageClickListener?.let {
                    article.isImageSaved = !article.isImageSaved
                    setImageBackground(article, clickToSaveImageViewId)
                    it(article)
                }
            }

            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }

        onBindComplete = true
    }

    private fun setImageBackground(article: HackerStory?, view: ImageView) {
        if (article?.isImageSaved!!) {
            view.setImageResource(R.drawable.ic_baseline_star_selected_24)
        } else {
            view.setImageResource(R.drawable.ic_baseline_star_not_selected_24)
        }
    }

    fun setOnItemClickListener(listener: (HackerStory) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnImageClickListener(listener: (HackerStory) -> Unit) {
        onSaveImageClickListener = listener
    }


    fun submitList(stores: List<HackerStory>) {
        differ.submitList(stores)
        fullList.clear()
        fullList.addAll(stores.toMutableList())
    }

    fun filter(query: String?) {
        if (query == null || query.isEmpty()) {
                differ.submitList(fullList)
        } else {
            val lowercaseQuery = query.toLowerCase()
            differ.submitList((
                    fullList.filter {
                    it.title?.toLowerCase()?.contains(lowercaseQuery)!!
                }))
        }

//        if (onBindComplete)
//            notifyDataSetChanged()
    }
}
