package ru.hse.elysiumapp.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import ru.hse.elysiumapp.R
import ru.hse.elysiumapp.databinding.SwipeItemBinding

class SwipeSongAdapter : BaseSongAdapter(R.layout.swipe_item) {
    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val binding = SwipeItemBinding.bind(holder.itemView)
        val song = songs[position]
        binding.apply {
            val text = "${song.title} - ${song.author}"
            tvPrimary.text = text

            root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }
}
