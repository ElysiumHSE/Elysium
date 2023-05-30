package ru.hse.elysiumapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.hse.elysiumapp.R
import ru.hse.elysiumapp.adapters.CommentAdapter
import ru.hse.elysiumapp.databinding.FragmentCommentsBinding
import javax.inject.Inject

@AndroidEntryPoint
class CommentsFragment : Fragment(R.layout.fragment_comments) {
    private lateinit var binding: FragmentCommentsBinding

    @Inject
    lateinit var commentAdapter: CommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCommentsBinding.bind(view)
        super.onViewCreated(binding.root, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() = binding.rvAllComments.apply {
        adapter = commentAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}