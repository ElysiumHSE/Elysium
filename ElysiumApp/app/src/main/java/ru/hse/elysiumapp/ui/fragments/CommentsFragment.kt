package ru.hse.elysiumapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.hse.elysiumapp.R
import ru.hse.elysiumapp.adapters.CommentAdapter
import ru.hse.elysiumapp.databinding.FragmentCommentsBinding
import javax.inject.Inject

@AndroidEntryPoint
class CommentsFragment(
    private val trackId: String
) : BottomSheetDialogFragment(R.layout.fragment_comments) {
    private lateinit var binding: FragmentCommentsBinding

    @Inject
    lateinit var commentAdapter: CommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCommentsBinding.bind(view)
        super.onViewCreated(binding.root, savedInstanceState)
        setupRecyclerView()

        if (commentAdapter.comments.isEmpty()) {
            hideComments()
        } else {
            showComments()
        }
    }

    private fun setupRecyclerView() = binding.rvAllComments.apply {
        adapter = commentAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showComments() {
        binding.rvAllComments.visibility = View.VISIBLE
        binding.tvNoComments.visibility = View.GONE
    }

    private fun hideComments() {
        binding.rvAllComments.visibility = View.GONE
        binding.tvNoComments.visibility = View.VISIBLE
    }
}