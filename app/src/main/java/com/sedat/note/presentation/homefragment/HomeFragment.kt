package com.sedat.note.presentation.homefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sedat.note.R
import com.sedat.note.databinding.FragmentHomeBinding
import com.sedat.note.presentation.homefragment.adapter.AdapterHomeFragment
import com.sedat.note.presentation.homefragment.viewmodel.ViewModelHomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: ViewModelHomeFragment by viewModels()
    @Inject
    lateinit var adapter: AdapterHomeFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        listeners()
        observeData()
    }

    private fun initRecyclerView() = with(binding){
        recylerNote.adapter = adapter
    }

    private fun listeners(){
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createNoteFragment)
        }
    }

    private fun observeData(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.noteList()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .distinctUntilChanged()
                .collect{
                    adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}