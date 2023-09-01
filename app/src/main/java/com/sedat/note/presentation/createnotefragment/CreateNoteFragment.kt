package com.sedat.note.presentation.createnotefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sedat.note.databinding.FragmentCreateNoteBinding
import com.sedat.note.domain.model.Note
import com.sedat.note.presentation.createnotefragment.adapter.AdapterCreateNote
import com.sedat.note.presentation.createnotefragment.viewmodel.ViewModelCreateNoteFragment
import com.sedat.note.util.Resource
import com.sedat.note.util.closeKeyboard
import com.sedat.note.util.hide
import com.sedat.note.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateNoteFragment : Fragment() {

    private var _binding: FragmentCreateNoteBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: ViewModelCreateNoteFragment by viewModels()
    @Inject
    lateinit var adapter: AdapterCreateNote

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNoteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeners()
        observeData()
    }

    private fun listeners() = with(binding){
        backBtn.setOnClickListener { findNavController().popBackStack() }

        saveBtn.setOnClickListener {
            if(edtNote.text.isNotEmpty()){
                val note = Note(id = 0, rootID = 1, text = edtNote.text.toString(), time = System.currentTimeMillis())
                viewModel.saveNote(note)
            }
        }
    }

    private fun observeData() = with(binding){
        viewModel.isSaveSuccessful.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Loading -> progressbar.show()
                is Resource.Success ->{
                    progressbar.hide()
                    requireActivity().closeKeyboard()
                }
                is Resource.Error ->{
                    progressbar.hide()
                    requireActivity().closeKeyboard()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}