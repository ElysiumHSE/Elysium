package ru.hse.elysiumapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hse.elysiumapp.R
import ru.hse.elysiumapp.data.entities.Comment
import ru.hse.elysiumapp.databinding.CommentItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var comments: List<Comment> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.comment_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val binding = CommentItemBinding.bind(holder.itemView)
        val comment = comments[position]
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        binding.apply {
            tvUsername.text = comment.username
            tvDatetime.text = dateFormat.format(
                Date(
                    comment.publicationDatetime?.time ?: 0
                )
            )
            tvContent.text = comment.content
        }
    }
}