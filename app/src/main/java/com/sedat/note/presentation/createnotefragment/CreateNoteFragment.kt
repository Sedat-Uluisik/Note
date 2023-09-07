package com.sedat.note.presentation.createnotefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sedat.note.R
import com.sedat.note.databinding.FragmentCreateNoteBinding
import com.sedat.note.domain.model.CustomType
import com.sedat.note.domain.model.Note
import com.sedat.note.presentation.createnotefragment.viewmodel.ViewModelCreateNoteFragment
import com.sedat.note.util.Resource
import com.sedat.note.util.afterTextChange
import com.sedat.note.util.closeKeyboard
import com.sedat.note.util.hide
import com.sedat.note.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateNoteFragment : Fragment() {

    private var _binding: FragmentCreateNoteBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: ViewModelCreateNoteFragment by viewModels()
    private val args: CreateNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNoteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(args.selectedNoteId != -1 && args.type == CustomType.UPDATE_NOTE)
            viewModel.getNoteWithID(args.selectedNoteId)

        listeners()
        observeData()
    }

    private fun listeners() = with(binding){
        backBtn.setOnClickListener { findNavController().popBackStack() }

        var job: Job ?= null
        edtNote.afterTextChange {
            job?.cancel()
            job = lifecycleScope.launch {
                delay(500)
                if (it.isNotEmpty())
                    saveBtn.show()
                else
                    saveBtn.hide()
            }

        }


        saveBtn.setOnClickListener {
            if(args.type == CustomType.UPDATE_NOTE && args.selectedNoteId != -1)
                updateCurrentNote(binding.edtNote.text.toString())
            else if(args.type == CustomType.CREATE_NEW_NOTE)
                createNewNote(binding.edtNote.text.toString())
            else if(args.type == CustomType.ADD_SUB_NOTE && args.selectedNoteId != -1)
                addSubNote(binding.edtNote.text.toString(), args.selectedNoteId)
        }
    }

    private fun createNewNote(_text: String){
        if(_text.isNotEmpty()){
            val note = Note(id = 0, rootID = -1, text = _text, time = System.currentTimeMillis())
            viewModel.saveNote(note)
        }
    }

    private fun updateCurrentNote(_text: String){
        if(args.selectedNoteId != -1 && _text.isNotEmpty())
            viewModel.updateNote(args.selectedNoteId, _text, System.currentTimeMillis())
    }

    private fun addSubNote(_text: String, _rootID: Int){
        if(_text.isNotEmpty() && _rootID != -1){
            val note = Note(id = 0, rootID = _rootID, text = _text, time = System.currentTimeMillis())
            viewModel.saveSubNote(note, _rootID)
        }
    }

    private fun observeData() = with(binding){
        viewModel.isSaveSuccessful.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Loading -> progressbar.show()
                is Resource.Success ->{
                    saveBtn.hide()
                    progressbar.hide()
                    requireActivity().closeKeyboard()
                }
                is Resource.Error ->{
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                    progressbar.hide()
                    requireActivity().closeKeyboard()
                }
            }
        }

        viewModel.selectedNote.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Loading -> {}
                is Resource.Success ->{
                    resource.data?.let {
                        binding.edtNote.setText(it.text)
                        saveBtn.show()
                    } ?: Toast.makeText(requireContext(), getString(R.string.selected_note_not_found), Toast.LENGTH_LONG).show()

                }
                is Resource.Error ->{
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}