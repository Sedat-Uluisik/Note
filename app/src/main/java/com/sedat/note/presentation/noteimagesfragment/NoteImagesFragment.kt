package com.sedat.note.presentation.noteimagesfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sedat.note.R
import com.sedat.note.databinding.FragmentNoteImagesBinding
import com.sedat.note.presentation.noteimagesfragment.adapter.AdapterNoteImagesFragment
import com.sedat.note.presentation.noteimagesfragment.viewmodel.ViewModelNoteImages
import com.sedat.note.util.Resource
import com.sedat.note.util.hide
import com.sedat.note.util.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoteImagesFragment : Fragment() {

    private var _binding: FragmentNoteImagesBinding ?= null
    private val binding get() = _binding!!

    @Inject
    lateinit var adapter: AdapterNoteImagesFragment
    @Inject
    lateinit var glide: RequestManager
    private val viewModel: ViewModelNoteImages by viewModels()
    private val args: NoteImagesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteImagesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        observe()
        listeners()

        if(args.noteID != -2)
            viewModel.getNoteImages(args.noteID)
    }

    private fun listeners() {
        adapter.itemClick {imagePath ->
            binding.recyclerImages.hide()
            binding.imgZoom.show()
            glide
                .load(imagePath)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.imgZoom)
        }

        binding.imgZoom.setOnClickListener {
            binding.imgZoom.setImageDrawable(null)
            binding.imgZoom.hide()
            binding.recyclerImages.show()
        }
    }

    private fun observe(){
        viewModel.noteImages.observe(viewLifecycleOwner){
            when(it){
                is Resource.Error ->{
                    adapter.submitList(listOf())
                }
                is Resource.Success ->{
                    adapter.submitList(it.data ?: listOf())
                }
                is Resource.Loading ->{}
            }
        }
    }

    private fun initRecyclerView(){
        binding.recyclerImages.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}