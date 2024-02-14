package com.sedat.note.presentation.createnotefragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sedat.note.R
import com.sedat.note.databinding.FragmentCreateNoteBinding
import com.sedat.note.domain.model.ActionType
import com.sedat.note.domain.model.NoteDto
import com.sedat.note.presentation.createnotefragment.adapter.AdapterColors
import com.sedat.note.presentation.createnotefragment.viewmodel.ViewModelCreateNoteFragment
import com.sedat.note.util.Resource
import com.sedat.note.util.TypeConverter
import com.sedat.note.util.afterTextChange
import com.sedat.note.util.closeKeyboard
import com.sedat.note.util.hide
import com.sedat.note.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CreateNoteFragment : Fragment() {

    private var _binding: FragmentCreateNoteBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: ViewModelCreateNoteFragment by viewModels()
    private val args: CreateNoteFragmentArgs by navArgs()

    private var createdNoteID: Int ?= null

    @Inject
    lateinit var adapterColors: AdapterColors
    private var noteItemBackGroundColor: IntArray = gradientColors().first()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNoteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerColors.adapter = adapterColors
        adapterColors.submitList(gradientColors())
        setGradientColor(noteItemBackGroundColor)

        if(args.selectedNoteId != -1 && args.type == ActionType.UPDATE_NOTE)
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
                delay(350)
                if (it.isNotEmpty())
                    saveBtn.show()
                else
                    saveBtn.hide()
            }

        }


        saveBtn.setOnClickListener {
            if(args.type == ActionType.UPDATE_NOTE && args.selectedNoteId != -1)
                updateCurrentNote(binding.edtNote.text.toString())
            else if(args.type == ActionType.CREATE_NEW_NOTE)
                if(createdNoteID == null)
                    createNewNote(binding.edtNote.text.toString())
                else
                    updateCurrentNote(binding.edtNote.text.toString())
            else if(args.type == ActionType.ADD_SUB_NOTE && args.selectedNoteId != -1)
                if(createdNoteID == null)
                    addSubNote(binding.edtNote.text.toString(), args.selectedNoteId)
                else
                    updateCurrentNote(binding.edtNote.text.toString())
        }

        adapterColors.itemClick {
            setGradientColor(it)
            noteItemBackGroundColor = it

            if(binding.edtNote.text.isNotEmpty())
                saveBtn.show()
        }
    }

    private fun createNewNote(_text: String){
        if(_text.isNotEmpty()){
            val note = NoteDto(id = 0, rootID = -1, text = _text, time = System.currentTimeMillis(), TypeConverter().toString(noteItemBackGroundColor))
            viewModel.saveNote(note)
        }
    }

    private fun updateCurrentNote(_text: String){
        if(createdNoteID != null && _text.isNotEmpty())
            viewModel.updateNote(createdNoteID!!, _text, System.currentTimeMillis(), TypeConverter().toString(noteItemBackGroundColor))
        else if(args.selectedNoteId != -1 && _text.isNotEmpty())
            viewModel.updateNote(args.selectedNoteId, _text, System.currentTimeMillis(), TypeConverter().toString(noteItemBackGroundColor))
    }

    private fun addSubNote(_text: String, _rootID: Int){
        if(_text.isNotEmpty() && _rootID != -1){
            val note = NoteDto(id = 0, rootID = _rootID, text = _text, time = System.currentTimeMillis(), TypeConverter().toString(noteItemBackGroundColor))
            viewModel.saveSubNote(note, _rootID)
        }
    }

    private fun observeData() = with(binding){
        viewModel.isSaveSuccessful.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Loading -> progressbar.show()
                is Resource.Success ->{

                     if((args.type == ActionType.ADD_SUB_NOTE || args.type == ActionType.CREATE_NEW_NOTE) && createdNoteID == null)
                         createdNoteID = resource.data?.toInt()

                    saveBtn.hide()
                    progressbar.hide()
                    requireActivity().closeKeyboard()
                }
                is Resource.Error ->{
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                    progressbar.hide()
                    requireActivity().closeKeyboard()
                }
                else -> {}
            }
        }

        viewModel.selectedNote.observe(viewLifecycleOwner){resource ->
            when(resource){
                is Resource.Loading -> {}
                is Resource.Success ->{
                    resource.data?.let {
                        binding.edtNote.setText(it.text)
                        setGradientColor(TypeConverter().fromString(it.color))
                        noteItemBackGroundColor = TypeConverter().fromString(it.color)
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
    private fun gradientColors() = listOf(
        intArrayOf(
            Color.parseColor("#ff9a9e"),
            Color.parseColor("#fad0c4")
        ),
        intArrayOf(
            Color.parseColor("#f6d365"),
            Color.parseColor("#fda085")
        ),
        intArrayOf(
            Color.parseColor("#a1c4fd"),
            Color.parseColor("#c2e9fb")
        ),
        intArrayOf(
            Color.parseColor("#84fab0"),
            Color.parseColor("#8fd3f4") //pastel açık
        ),
        intArrayOf(
            Color.parseColor("#667eea"),
            Color.parseColor("#764ba2")
        ),
        intArrayOf(
            Color.parseColor("#6a11cb"),
            Color.parseColor("#2575fc")
        ),
        intArrayOf(
            Color.parseColor("#0ba360"),
            Color.parseColor("#3cba92") //pastel koyu
        ),
        intArrayOf(
            Color.parseColor("#fbc2eb"),
            Color.parseColor("#a18cd1")
        ),
        intArrayOf(
            Color.parseColor("#a6c0fe"),
            Color.parseColor("#f68084")
        ),
        intArrayOf(
            Color.parseColor("#fddb92"),
            Color.parseColor("#d1fdff")
        ),
        intArrayOf(
            Color.parseColor("#b721ff"),
            Color.parseColor("#21d4fd") //grad açık
        ),
        intArrayOf(
            Color.parseColor("#30cfd0"),
            Color.parseColor("#330867")
        ),
        intArrayOf(
            Color.parseColor("#2af598"),
            Color.parseColor("#009efd")
        ),
        intArrayOf(
            Color.parseColor("#13547a"),
            Color.parseColor("#80d0c7") //grad koyu
        )
    )

    private fun setGradientColor(color: IntArray){
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, color)

        gradientDrawable.cornerRadius = 0f
        gradientDrawable.setStroke(
            0,
            ContextCompat.getColor(this.binding.root.context, R.color.grey)
        )

        binding.rootLayout.background = gradientDrawable

        //change status bar color

        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color[0]
    }


}