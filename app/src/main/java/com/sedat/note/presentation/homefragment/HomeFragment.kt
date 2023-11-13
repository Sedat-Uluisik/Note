package com.sedat.note.presentation.homefragment

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sedat.note.R
import com.sedat.note.databinding.FragmentHomeBinding
import com.sedat.note.domain.model.ActionType
import com.sedat.note.presentation.homefragment.adapter.AdapterHomeFragment
import com.sedat.note.presentation.homefragment.viewmodel.ViewModelHomeFragment
import com.sedat.note.util.ButtonsClick
import com.sedat.note.util.CustomAlert
import com.sedat.note.util.afterTextChange
import com.sedat.note.util.hide
import com.sedat.note.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
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
    private var selectedNoteID: Int = -2
    private var rootIDList: ArrayList<Int> = arrayListOf()
    private var searchState = false

    private val permissionList = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val permissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissionss ->
        val permissionCamera = permissionss.getValue(Manifest.permission.CAMERA)
        val permissionStorage = permissionss.getValue(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permissionCamera && permissionStorage){
            /*Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.selectImageFragment, null, null)*/

           if(selectedNoteID != -2){
               val action = HomeFragmentDirections.actionHomeFragmentToSelectImageFragment(noteId = selectedNoteID)
               selectedNoteID = -2
               findNavController().navigate(action)
           }else
               Toast.makeText(requireContext(), getString(R.string.note_is_not_selected), Toast.LENGTH_LONG).show()
        }
        else{
            if(!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Snackbar.make(
                    binding.root,
                    getString(R.string.permission_required_from_app_settings),
                    Snackbar.LENGTH_LONG
                ).setAction("Retry") {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    this.startActivity(intent)
                }.show()
            }else
                displayErrorMessageForPermission()
        }
    }

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
            val action = HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(ActionType.CREATE_NEW_NOTE)
            findNavController().navigate(action)
        }

        adapter.moreBtnClick {note, type ->
            when(type){
                ButtonsClick.MORE ->{
                    CustomAlert(requireContext()).showCustomAlert {
                        when(it){
                            ButtonsClick.ADD_IMAGE ->{
                                selectedNoteID = note.id
                                permissionRequestLauncher.launch(permissionList)
                            }
                            ButtonsClick.ADD_SUB_NOTE ->{
                                val action = HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(ActionType.ADD_SUB_NOTE, selectedNoteId = note.id)
                                findNavController().navigate(action)
                            }
                            ButtonsClick.DELETE_NOTE ->{
                                CustomAlert(requireContext()).showDefaultAlert(getString(R.string.delete), getString(R.string.is_delete_note)) {
                                    if(it){
                                        viewModel.deleteNoteAndSubNotes(note.id)
                                        viewModel.getSubNotes(note.id)
                                    }
                                }
                            }
                            else ->{}
                        }
                    }
                }
                ButtonsClick.SHOW_SUB_NOTES ->{
                    rootIDList.add(note.rootID)
                    viewModel.getSubNotes(note.id)
                }
                ButtonsClick.SHOW_IMAGE ->{
                    val action = HomeFragmentDirections.actionHomeFragmentToNoteImagesFragment(noteID = note.id)
                    findNavController().navigate(action)
                }
                ButtonsClick.RECYCLERVIEW_ITEM_CLICK ->{
                    val action = HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(type = ActionType.UPDATE_NOTE, selectedNoteId = note.id)
                    findNavController().navigate(action)
                }
                else ->{}
            }
        }

        binding.backBtnForSubNotes.setOnClickListener {
            backBtnClick()
        }

        var job: Job?= null
        binding.serachEdittext.afterTextChange { txt ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(500)
                searchState = if (txt.isNotEmpty()) {
                    viewModel.searchNote(txt)
                    true
                } else {
                    viewModel.getMainNotes()
                    false
                }
            }

        }

    }

    private fun observeData(){
        if(!searchState){
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.noteList()
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .distinctUntilChanged()
                    .debounce(500L)
                    .collect{
                        adapter.submitList(it)
                        binding.backBtnForSubNotes.hide()

                        println("-----------------------------")
                    }
            }
        }

        viewModel.subNoteList.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { list ->
                if(list.isNotEmpty()){
                    adapter.submitList(list)
                    if(rootIDList.size > 0)
                        binding.backBtnForSubNotes.show()

                    println("**********************************")
                }
            }
        }
    }

    private fun backBtnClick(){
        println(rootIDList.size)
        if(rootIDList.size == 1){
            rootIDList.clear()
            binding.backBtnForSubNotes.hide()
            viewModel.getMainNotes()
        }
        else{
            if(rootIDList.isEmpty() && binding.backBtnForSubNotes.isVisible)
                binding.backBtnForSubNotes.hide()
            else if(rootIDList.isEmpty() && !binding.backBtnForSubNotes.isVisible)
                requireActivity().finish()
            else{
                viewModel.getSubNotes(rootIDList.last())
                rootIDList.removeLast()
            }
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

    private fun displayErrorMessageForPermission() {
        Snackbar.make(
            binding.root,
            getString(R.string.permission_required),
            Snackbar.LENGTH_LONG
        ).setAction("Retry") {
            permissionRequestLauncher.launch(permissionList)
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}