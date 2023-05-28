package ru.hse.elysiumapp.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import ru.hse.elysiumapp.R
import ru.hse.elysiumapp.databinding.ListItemBinding
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseSongAdapter(R.layout.list_item) {
    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val binding = ListItemBinding.bind(holder.itemView)
        val song = songs[position]
        binding.apply {
            tvPrimary.text = song.title
            tvSecondary.text = song.author
            glide.load(song.imageUrl).into(ivItemImage)

            root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }
}
