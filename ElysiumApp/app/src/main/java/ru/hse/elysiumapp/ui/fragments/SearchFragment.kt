package ru.hse.elysiumapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.hse.elysiumapp.R
import ru.hse.elysiumapp.databinding.FragmentSearchBinding
import ru.hse.elysiumapp.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchBinding.bind(view)
        super.onViewCreated(binding.root, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}