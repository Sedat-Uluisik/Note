package com.sedat.note.presentation.selectimagefragment

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowMetrics
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.google.common.util.concurrent.ListenableFuture
import com.sedat.note.R
import com.sedat.note.databinding.FragmentSelectImageBinding
import com.sedat.note.databinding.ItemLayoutErrorForSnackBarBinding
import com.sedat.note.presentation.selectimagefragment.viewmodel.ViewModelSelectImage
import com.sedat.note.util.Resource
import com.sedat.note.util.hide
import com.sedat.note.util.show
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

@AndroidEntryPoint
class SelectImageFragment : Fragment() {

    private var _binding: FragmentSelectImageBinding ?= null
    private val binding get() = _binding!!
    private val viewModel: ViewModelSelectImage by viewModels()

    private lateinit var processCameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var processCameraProvider: ProcessCameraProvider

    private val args: SelectImageFragmentArgs by navArgs()
    private var currentCamera = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processCameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectImageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeners()
        observe()
    }

    private fun listeners(){
        processCameraProviderFuture.addListener( {
            processCameraProvider = processCameraProviderFuture.get()
            binding.viewFinder.post { setupCamera() }
        }, ContextCompat.getMainExecutor(requireContext()))

        binding.btnGetImage.setOnClickListener {
            if (::processCameraProvider.isInitialized) {
                processCameraProvider.unbindAll()
            }

            binding.btnChangeCamera.hide()
            binding.btnDoneSelectImage.show()
            binding.btnDontSelectImage.show()
        }

        binding.btnDoneSelectImage.setOnClickListener {
           if(args.noteId != -2){
               val bitmap = binding.viewFinder.bitmap
               bitmap?.let {
                   val imagePath = saveImageToFile(it)
                   if(imagePath.isNotEmpty()) {
                       val description = binding.edtImageNote.text.toString().ifEmpty { "" }
                       viewModel.saveImagePathToRoomDB(args.noteId, imagePath, description)
                   }
                   else
                       showErrorSnackBar(getString(R.string.image_could_not_be_saved_to_file))
               }
           }else{
               showErrorSnackBar(getString(R.string.selection_failed_please_try_again))
           }
        }

        binding.btnDontSelectImage.setOnClickListener {

            setupCamera()
            binding.btnDoneSelectImage.hide()
            binding.btnDontSelectImage.hide()
            binding.btnChangeCamera.show()
        }

        binding.btnChangeCamera.setOnClickListener {

            val rotateAnimation = RotateAnimation(
                180f,
                0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            ).apply {
                duration = 400
            }
            it.startAnimation(rotateAnimation)

            currentCamera = if(currentCamera == CameraSelector.DEFAULT_BACK_CAMERA)
                CameraSelector.DEFAULT_FRONT_CAMERA
            else
                CameraSelector.DEFAULT_BACK_CAMERA

            setupCamera()
        }
    }

    private fun observe(){
        viewModel.isImageSaveToRoomDBSuccessful.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading ->{}
                is Resource.Success ->{
                    binding.btnDoneSelectImage.hide()
                    binding.btnDontSelectImage.hide()
                    setupCamera()
                    binding.btnChangeCamera.show()
                    binding.edtImageNote.setText("")
                }
                is Resource.Error -> showErrorSnackBar(it.message ?: getString(R.string.image_path_not_save_to_room_db))
            }
        }
    }

    private fun showErrorSnackBar(message: String){
        val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
        val customView = ItemLayoutErrorForSnackBarBinding.inflate(
            LayoutInflater.from(requireContext())
        )
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)
       snackbar.apply {
           (view as ViewGroup).addView(customView.root)
           customView.txtErrorMessage.text = message
           show()
       }
    }

    private fun setupCamera() {
        val display = binding.viewFinder.display
        val metrics = getMetrics()
        processCameraProvider.unbindAll()
        val camera = processCameraProvider.bindToLifecycle(
            this,
            currentCamera,
            buildPreviewUseCase(display, metrics),
            buildImageCaptureUseCase(display, metrics),
            buildImageAnalysisUseCase(display, metrics)
        )

        setupTapForFocus(camera)

        /*camera.cameraInfo.zoomState.observe(viewLifecycleOwner){
            println(it.zoomRatio)
        }*/
    }

    private fun buildPreviewUseCase(display: Display, metrics: Pair<Int, Int>): Preview {
        val preview = Preview.Builder()
            .setTargetRotation(display.rotation)
            .setTargetResolution(Size(metrics.first, metrics.second))
            .build()
        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        return preview
    }

    private fun buildImageCaptureUseCase(display: Display, metrics: Pair<Int, Int>): ImageCapture {
        return ImageCapture.Builder()
            .setTargetRotation(display.rotation)
            .setTargetResolution(Size(metrics.first, metrics.second))
            .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    private fun saveImageToFile(bitmap: Bitmap): String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dir = File(requireContext().applicationContext.filesDir, "Pictures")
        if(!dir.exists())
            dir.mkdir()

        dir.setReadable(true)
        dir.setWritable(true)

        val file = File(dir, "${dateFormat.format(System.currentTimeMillis())}.jpg")
        file.setReadable(true)
        file.setWritable(true)

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e:Exception){
            e.printStackTrace()
        }

        return if(file.exists()) file.absolutePath else ""
    }

    private fun buildImageAnalysisUseCase(display: Display, metrics: Pair<Int, Int>): ImageAnalysis {
        val analysis = ImageAnalysis.Builder()
            .setTargetRotation(display.rotation)
            .setTargetResolution(Size(metrics.first, metrics.second))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
            .setImageQueueDepth(10)
            .build()
        analysis.setAnalyzer(
            Executors.newSingleThreadExecutor()
        ) { proxy ->
            proxy.close()
        }
        return analysis
    }

    private fun getMetrics(): Pair<Int, Int>{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            val width = windowMetrics.bounds.width() - insets.left - insets.right
            val height = windowMetrics.bounds.height() - insets.top - insets.bottom
            Pair(width, height)
        }else{
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTapForFocus(camera: Camera) {

        //camera zoom
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 0F

                val delta = detector.scaleFactor

                camera.cameraControl.setZoomRatio(currentZoomRatio * delta)

                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(requireContext(), listener) //camera zoom

        binding.viewFinder.setOnTouchListener { _, event ->

            scaleGestureDetector.onTouchEvent(event) //camera zoom

            //camera focus
            when(event.action){
                MotionEvent.ACTION_DOWN -> return@setOnTouchListener true
                MotionEvent.ACTION_UP ->{
                    val point1 = binding.viewFinder.meteringPointFactory.createPoint(event.x, event.y) //dokunulan yere odak atmak için kullanılır.
                    val action1 = FocusMeteringAction.Builder(point1).build()
                    camera.cameraControl.startFocusAndMetering(action1)

                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener false
            }
        }

        /*binding.zoomSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                camera.cameraControl.setZoomRatio(progress.toFloat())

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::processCameraProvider.isInitialized) {
            processCameraProvider.unbindAll()
        }
        _binding = null
    }


}