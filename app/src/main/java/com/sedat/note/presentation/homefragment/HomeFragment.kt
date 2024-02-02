package com.sedat.note.presentation.homefragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
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
import com.sedat.note.domain.model.Note
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
    private var typeForImageSelectMode: Boolean = true //true -> gallery, false -> camera
    private var rootIDList: ArrayList<Int> = arrayListOf()
    private var searchState = false

    private val permissionList = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val permissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissionss ->
        val permissionCamera = permissionss.getValue(Manifest.permission.CAMERA)
        val permissionStorage = permissionss.getValue(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionStorageRead = permissionss.getValue(Manifest.permission.READ_EXTERNAL_STORAGE)
        if(permissionCamera && permissionStorage && permissionStorageRead){

           if(selectedNoteID != -2){
               val action = HomeFragmentDirections.actionHomeFragmentToSelectImageFragment(
                   noteId = selectedNoteID,
                   type = if(typeForImageSelectMode) ButtonsClick.IMAGE_FOR_GALLERY else ButtonsClick.IMAGE_FOR_CAMERA
               )
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

        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#EFEFEF")
    }

    private fun initRecyclerView() = with(binding){
        recylerNote.adapter = adapter
    }

    private fun listeners(){
        binding.fab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(ActionType.CREATE_NEW_NOTE)
            findNavController().navigate(action)
        }

        adapter.moreBtnClick {view, note, type ->
            when(type){
                ButtonsClick.MORE ->{
                    /*CustomAlert(requireContext()).showCustomAlert {
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
                    }*/
                    popupMenuForMoreButton(view, note)
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
                    }
            }
        }

        viewModel.subNoteList.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { list ->
                if(list.isNotEmpty()){
                    adapter.submitList(list)
                    if(rootIDList.size > 0)
                        binding.backBtnForSubNotes.show()
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

    @SuppressLint("DiscouragedPrivateApi")
    fun popupMenuForMoreButton(view: View, note: Note){
        val popupMenu = PopupMenu(requireContext(), view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { it ->
            when(it.itemId){
                R.id.add_sub_note ->{
                    val action = HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(ActionType.ADD_SUB_NOTE, selectedNoteId = note.id)
                    findNavController().navigate(action)
                }
                R.id.add_image ->{
                    selectedNoteID = note.id
                    //permissionRequestLauncher.launch(permissionList)
                    CustomAlert(requireContext()).showAlertForImageSelect { buttonsClick ->
                        when(buttonsClick){
                            ButtonsClick.IMAGE_FOR_GALLERY ->{
                                typeForImageSelectMode = true
                                permissionRequestLauncher.launch(permissionList)
                            }
                            ButtonsClick.IMAGE_FOR_CAMERA ->{
                                typeForImageSelectMode = false
                                permissionRequestLauncher.launch(permissionList)
                            }
                            else ->{}
                        }
                    }
                }
                R.id.delete_note ->{
                    CustomAlert(requireContext()).showDefaultAlert(getString(R.string.delete), getString(R.string.is_delete_note)) {
                        if(it){
                            viewModel.deleteNoteAndSubNotes(note.id)
                            viewModel.getSubNotes(note.id)
                        }
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }

        try {
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenu)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            popupMenu.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}