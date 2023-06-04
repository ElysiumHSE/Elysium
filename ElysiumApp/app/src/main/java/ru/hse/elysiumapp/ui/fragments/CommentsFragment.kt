package ru.hse.elysiumapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.hse.elysiumapp.R
import ru.hse.elysiumapp.adapters.CommentAdapter
import ru.hse.elysiumapp.databinding.FragmentCommentsBinding
import ru.hse.elysiumapp.other.Status
import ru.hse.elysiumapp.ui.viewmodels.CommentViewModel
import javax.inject.Inject

@AndroidEntryPoint
class CommentsFragment(
    private val trackId: String
) : BottomSheetDialogFragment(R.layout.fragment_comments) {
    private lateinit var binding: FragmentCommentsBinding
    private lateinit var commentViewModel: CommentViewModel

    @Inject
    lateinit var commentAdapter: CommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCommentsBinding.bind(view)
        super.onViewCreated(binding.root, savedInstanceState)
        commentViewModel = ViewModelProvider(requireActivity())[CommentViewModel::class.java]

        setupRecyclerView()
        subscribeToObservers()

        commentViewModel.loadComments(trackId.toInt())
    }

    private fun setupRecyclerView() = binding.rvAllComments.apply {
        adapter = commentAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        commentViewModel.commentItems.observe(this@CommentsFragment) {result ->
            when (result.status) {
                Status.SUCCESS -> {
                    result.data?.let { comments ->
                        commentAdapter.comments = comments
                    }
                    if (commentAdapter.comments.isEmpty()) {
                        showNoComments()
                    } else {
                        showComments()
                    }
                }
                Status.ERROR -> showNoComments()
                Status.LOADING -> showLoading()
            }
        }
    }

    private fun showComments() {
        binding.allCommentsProgressBar.visibility = View.GONE
        binding.tvNoComments.visibility = View.GONE
        binding.rvAllComments.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.allCommentsProgressBar.visibility = View.VISIBLE
        binding.tvNoComments.visibility = View.GONE
        binding.rvAllComments.visibility = View.GONE
    }

    private fun showNoComments() {
        binding.allCommentsProgressBar.visibility = View.GONE
        binding.tvNoComments.visibility = View.VISIBLE
        binding.rvAllComments.visibility = View.GONE
    }
}