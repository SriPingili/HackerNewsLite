package com.androideveloper.thenewsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.model.HackerStory
import kotlinx.android.synthetic.main.item_article_preview.view.*
import java.lang.Exception
import java.net.URL

class HackerFeedAdapter : RecyclerView.Adapter<HackerFeedAdapter.ArticleViewHolder>() {
    private var onItemClickListener: ((HackerStory) -> Unit)? = null

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
            }catch (e: Exception){

            }


            urlTextViewId.text = url
            authorTextViewId.text = "by ${article.by}"
            commentsCountTextViewId.text = article.kids?.size.toString()
            createdAtTextViewId.text = "Yesterday"

            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (HackerStory) -> Unit) {
        onItemClickListener = listener
    }
}