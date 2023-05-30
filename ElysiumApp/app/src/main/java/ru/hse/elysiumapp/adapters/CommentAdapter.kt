package ru.hse.elysiumapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hse.elysiumapp.data.entities.Comment
import ru.hse.elysiumapp.databinding.CommentItemBinding

class CommentAdapter(
    private val layoutId: Int
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var comments: List<Comment> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layoutId,
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
        binding.apply {
            tvNickname.text = comment.nickname
            tvDatetime.text = comment.publicationDatetime.toString()
            tvComment.text = comment.text
        }
    }
}