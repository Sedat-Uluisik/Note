package com.sedat.note.presentation.homefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sedat.note.R
import com.sedat.note.databinding.FragmentHomeBinding
import com.sedat.note.domain.model.CustomType
import com.sedat.note.presentation.homefragment.adapter.AdapterHomeFragment
import com.sedat.note.presentation.homefragment.viewmodel.ViewModelHomeFragment
import com.sedat.note.util.CustomAlert
import com.sedat.note.util.hide
import com.sedat.note.util.show
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
    private var rootIDList: ArrayList<Int> = arrayListOf()

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
        onBackPressed()
    }

    private fun initRecyclerView() = with(binding){
        recylerNote.adapter = adapter
    }

    private fun listeners(){
        binding.fab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(CustomType.CREATE_NEW_NOTE)
            findNavController().navigate(action)
        }

        adapter.moreBtnClick {notNoteWithSubNoteInfo ->
            CustomAlert(requireContext()).show {
                when(it){
                    CustomAlert.ButtonsClick.ADD_IMAGE ->{

                    }
                    CustomAlert.ButtonsClick.ADD_SUB_NOTE ->{
                        val action = HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(CustomType.ADD_SUB_NOTE, selectedNoteId = notNoteWithSubNoteInfo.note.id)
                        findNavController().navigate(action)
                    }
                    CustomAlert.ButtonsClick.UPDATE_NOTE ->{
                        val action = HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(type = CustomType.UPDATE_NOTE, selectedNoteId = notNoteWithSubNoteInfo.note.id)
                        findNavController().navigate(action)
                    }
                }
            }
        }

        adapter.itemClick {noteWithSubNoteInfo ->
            rootIDList.add(noteWithSubNoteInfo.note.rootID)
            viewModel.getSubNotes(noteWithSubNoteInfo.note.id)
        }

        binding.backBtnForSubNotes.setOnClickListener {
            backBtnClick()
        }

    }

    private fun observeData(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.noteList()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .distinctUntilChanged()
                .collect{
                    println("observe submit")
                    adapter.submitList(it)
                    binding.backBtnForSubNotes.hide()
            }
        }

        viewModel.subNoteList.observe(viewLifecycleOwner){
            adapter.submitList(it)
            if(rootIDList.size > 0)
                binding.backBtnForSubNotes.show()
        }
    }

    private fun backBtnClick(){
        if(rootIDList.size == 1){
            rootIDList.clear()
            binding.backBtnForSubNotes.hide()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.noteList()
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .distinctUntilChanged()
                    .collect{
                        println("back btn submit")
                        adapter.submitList(it)
                    }
            }
        }
        else{
            viewModel.getSubNotes(rootIDList.last())
            rootIDList.removeLast()
            if(rootIDList.isEmpty())
                binding.backBtnForSubNotes.hide()
        }
    }

    private fun onBackPressed() {
        val onBackPressCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backBtnClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressCallBack)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}