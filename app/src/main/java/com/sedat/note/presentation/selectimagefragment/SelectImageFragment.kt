package com.sedat.note.presentation.selectimagefragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.common.util.concurrent.ListenableFuture
import com.sedat.note.R
import com.sedat.note.databinding.FragmentSelectImageBinding
import com.sedat.note.util.FileCreator
import com.sedat.note.util.FileCreator.JPEG_FORMAT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executors


class SelectImageFragment : Fragment() {

    private var _binding: FragmentSelectImageBinding ?= null
    private val binding get() = _binding!!

    private lateinit var processCameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var processCameraProvider: ProcessCameraProvider

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

        processCameraProviderFuture.addListener( {
            processCameraProvider = processCameraProviderFuture.get()
            binding.viewFinder.post { setupCamera() }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun setupCamera() {
        processCameraProvider.unbindAll()
        val camera = processCameraProvider.bindToLifecycle(
            this,
            CameraSelector.DEFAULT_BACK_CAMERA,
            buildPreviewUseCase(),
            buildImageCaptureUseCase(),
            buildImageAnalysisUseCase())

        setupTapForFocus(camera)

        camera.cameraInfo.zoomState.observe(viewLifecycleOwner){
            println(it.zoomRatio)
        }
    }

    private fun buildPreviewUseCase(): Preview {
        val display = binding.viewFinder.display
        val metrics = DisplayMetrics().also { display.getMetrics(it) }
        val preview = Preview.Builder()
            .setTargetRotation(display.rotation)
            .setTargetResolution(Size(metrics.widthPixels, metrics.heightPixels))
            .build()
        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        return preview
    }

    private fun buildImageCaptureUseCase(): ImageCapture {
        val display = binding.viewFinder.display
        val metrics = DisplayMetrics().also { display.getMetrics(it) }
        val capture = ImageCapture.Builder()
            .setTargetRotation(display.rotation)
            .setTargetResolution(Size(metrics.widthPixels, metrics.heightPixels))
            .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()

        val executor = Executors.newSingleThreadExecutor()
        binding.btnGetImage.setOnClickListener {
            capture.takePicture(
                FileCreator.createTempFile(JPEG_FORMAT),
                executor,
                object : ImageCapture.OnImageSavedCallback {

                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        outputFileResults.savedUri?.let {

                            val bitmap = BitmapFactory.decodeStream(
                                requireActivity().contentResolver.openInputStream(it)
                            )

                            CoroutineScope(Dispatchers.Main).launch {
                                //(view as ImageView).setImageBitmap(bitmap)
                                /*Glide.with(requireContext())
                                    .load(it)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(binding.btnGetImage)*/
                            }

                            saveImageToFile(bitmap, "159357")

                            /*CoroutineScope(Dispatchers.Main).launch {
                                val arguments = GalleryFragment.arguments(it.path.toString())
                                Navigation.findNavController(requireActivity(), R.id.mainContent)
                                    .navigate(R.id.imagePreviewFragment, arguments)
                            }*/
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {

                    }
                })
        }
        return capture
    }

    private fun saveImageToFile(bitmap: Bitmap, uid: String): Uri {
        //val dir = File(requireContext().getExternalFilesDir("/"), "Pictures")
        val dir = File(requireContext().applicationContext.filesDir, "Pictures")
        //val dir = File(Environment.getExternalStoragePublicDirectory("/"), "Pictures")
        if(!dir.exists())
            dir.mkdir()

        dir.setReadable(true)
        dir.setWritable(true)

        val file = File(dir, "${uid}.jpg")
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

        //delete cached images
        requireContext().cacheDir.deleteRecursively()

        return Uri.parse(file.absolutePath)
    }

    private fun buildImageAnalysisUseCase(): ImageAnalysis {
        val display = binding.viewFinder.display
        val metrics = DisplayMetrics().also { display.getMetrics(it) }
        val analysis = ImageAnalysis.Builder()
            .setTargetRotation(display.rotation)
            .setTargetResolution(Size(metrics.widthPixels, metrics.heightPixels))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
            .setImageQueueDepth(10)
            .build()
        analysis.setAnalyzer(
            Executors.newSingleThreadExecutor(),
            ImageAnalysis.Analyzer { imageProxy ->
                Log.d("CameraFragment", "Image analysis result $imageProxy")
                imageProxy.close()
            })
        return analysis
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