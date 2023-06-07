package ru.hse.elysiumapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import ru.hse.elysiumapp.R
import ru.hse.elysiumapp.data.entities.Song
import ru.hse.elysiumapp.databinding.ListItemBinding
import javax.inject.Inject

class SearchAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var songs: List<Song> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val binding = ListItemBinding.bind(holder.itemView)
        val song = songs[position]

        binding.apply {
            tvPrimary.text = song.name
            tvSecondary.text = song.author
            glide.load(song.coverUrl).into(ivItemImage)
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}