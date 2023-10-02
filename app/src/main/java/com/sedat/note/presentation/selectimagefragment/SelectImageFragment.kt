package com.sedat.note.presentation.selectimagefragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
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
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.sedat.note.databinding.FragmentSelectImageBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors


class SelectImageFragment : Fragment() {

    private var _binding: FragmentSelectImageBinding ?= null
    private val binding get() = _binding!!

    private lateinit var processCameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var processCameraProvider: ProcessCameraProvider

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

            binding.btnDoneSelectImage.visibility = View.VISIBLE
            binding.btnDontSelectImage.visibility = View.VISIBLE
        }

        binding.btnDoneSelectImage.setOnClickListener {
            val bitmap = binding.viewFinder.bitmap
            bitmap?.let {
                saveImageToFile(it)
            }

            binding.btnDoneSelectImage.visibility = View.GONE
            binding.btnDontSelectImage.visibility = View.GONE
        }

        binding.btnDontSelectImage.setOnClickListener {

            setupCamera()
            binding.btnDoneSelectImage.visibility = View.GONE
            binding.btnDontSelectImage.visibility = View.GONE
        }

        binding.btnChangeCamera.setOnClickListener {
            currentCamera = if(currentCamera == CameraSelector.DEFAULT_BACK_CAMERA)
                CameraSelector.DEFAULT_FRONT_CAMERA
            else
                CameraSelector.DEFAULT_BACK_CAMERA

            setupCamera()
        }
    }

    private fun setupCamera() {
        processCameraProvider.unbindAll()
        val camera = processCameraProvider.bindToLifecycle(
            this,
            currentCamera,
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

        return ImageCapture.Builder()
            .setTargetRotation(display.rotation)
            .setTargetResolution(Size(metrics.widthPixels, metrics.heightPixels))
            .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    private fun saveImageToFile(bitmap: Bitmap): Uri {
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

        setupCamera()

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
            Executors.newSingleThreadExecutor()
        ) { proxy ->
            proxy.close()
        }
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