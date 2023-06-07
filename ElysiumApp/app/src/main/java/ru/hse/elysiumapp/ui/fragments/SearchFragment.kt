package ru.hse.elysiumapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.hse.elysiumapp.R
import ru.hse.elysiumapp.adapters.SearchAdapter
import ru.hse.elysiumapp.databinding.FragmentSearchBinding
import ru.hse.elysiumapp.other.Status
import ru.hse.elysiumapp.ui.viewmodels.MainViewModel
import ru.hse.elysiumapp.ui.viewmodels.SearchViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var searchAdapter: SearchAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchBinding.bind(view)
        super.onViewCreated(binding.root, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        setupRecyclerView()
        subscribeToObservers()

        binding.evInputRequest.doAfterTextChanged {
            val request = it.toString()
            searchViewModel.provideSearch(request)
        }
    }

    private fun setupRecyclerView() = binding.rvAllSongs.apply {
        adapter = searchAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        searchViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    result.data?.let { songs ->
                        searchAdapter.songs = songs
                    }
                    if (searchAdapter.songs.isEmpty()) {
                        showNothing()
                    } else {
                        showSongs()
                    }
                }
                Status.LOADING -> showLoading()
                Status.ERROR -> showNothing()
            }
            searchAdapter.notifyDataSetChanged()
        }
    }

    private fun showNothing() {
        binding.tvNothing.visibility = View.VISIBLE
        binding.rvAllSongs.visibility = View.GONE
        binding.allSongsProgressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.tvNothing.visibility = View.GONE
        binding.rvAllSongs.visibility = View.GONE
        binding.allSongsProgressBar.visibility = View.VISIBLE
    }

    private fun showSongs() {
        binding.tvNothing.visibility = View.GONE
        binding.rvAllSongs.visibility = View.VISIBLE
        binding.allSongsProgressBar.visibility = View.GONE
    }
}
