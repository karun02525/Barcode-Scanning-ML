package com.cameraxdemo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.cameraxdemo.databinding.ActivityScannerBinding
import com.google.common.util.concurrent.ListenableFuture

class ScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerBinding
    private lateinit var cameraSelector: CameraSelector
    private lateinit var cameraProviderFuture:ListenableFuture<ProcessCameraProvider>
    private lateinit var processCameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview:Preview
    private lateinit var imageAnalysis: ImageAnalysis



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraSelector=CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        cameraProviderFuture =ProcessCameraProvider.getInstance(this)


        cameraProviderFuture.addListener({
            processCameraProvider = cameraProviderFuture.get()
            //bind camera preview
            bindCameraPreview()
            bindInputAnalysis()
            },ContextCompat.getMainExecutor(this)
        )
    }

    private fun bindInputAnalysis() {
        val barcodeScanner:BarcodeScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

        imageAnalysis = ImageAnalysis.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()

        val cameraExecutor =Executors.newSingleThreadExecutor()
        imageAnalysis.setAnalyzer(cameraExecutor){imageProxy->
            processImageProxy(barcodeScanner,imageProxy)
        }

        processCameraProvider.bindToLifecycle(this,cameraSelector,imageAnalysis)

    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy){
        val inputImage = InputImage.fromMediaImage(imageProxy.image!!,imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(inputImage)
            .addOnSuccessListener {barcode->
                if(barcode.isNotEmpty()) {
                    onScan?.invoke(barcode)
                    onScan=null
                    finish()
                }
            }
            .addOnFailureListener {
                imageProxy.close()
                it.printStackTrace()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun bindCameraPreview(){
       cameraPreview=Preview.Builder()
           .setTargetRotation(binding.previewView.display.rotation)
           .build()

       cameraPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
       processCameraProvider.bindToLifecycle(this,cameraSelector,cameraPreview)

    }

    companion object{
        private var onScan:((barcodes:List<Barcode>)->Unit)?=null
        fun starScanner(context: Context,onScan:(barcodes:List<Barcode>)->Unit){
            this.onScan=onScan
            Intent(context,ScannerActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }

}
  /*  private lateinit var binding: ActivityScannerBinding
    private lateinit var cameraSelector: CameraSelector
    private lateinit var processCameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview: Preview
    private lateinit var imageAnalysis: ImageAnalysis

   // private val cameraXViewModel = viewModels<CameraXViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

      *//*  cameraXViewModel.value.processCameraProvider.observe(this) { provider ->
            processCameraProvider = provider
            bindCameraPreview()
            bindInputAnalyser()
        }*//*
    }

    private fun bindCameraPreview() {
        cameraPreview = Preview.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
        cameraPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
        try {
            processCameraProvider.bindToLifecycle(this, cameraSelector, cameraPreview)
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun bindInputAnalyser() {
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()

        val cameraExecutor = Executors.newSingleThreadExecutor()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        }

        try {
            processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis)
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    showBarcodeInfo(barcodes.first())
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message ?: it.toString())
            }.addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun showBarcodeInfo(barcode: Barcode) {
        when (barcode.valueType) {
            Barcode.TYPE_URL -> {
                binding.textViewQrType.text = "URL"
                binding.textViewQrContent.text = barcode.rawValue
            }
            Barcode.TYPE_CONTACT_INFO -> {
                binding.textViewQrType.text = "Contact"
                binding.textViewQrContent.text = barcode.contactInfo.toString()
            }
            else -> {
                binding.textViewQrType.text = "Other"
                binding.textViewQrContent.text = barcode.rawValue
            }
        }
    }

    companion object {
        private val TAG = ScannerActivity::class.simpleName

        fun startScanner(context: Context) {
            Intent(context, ScannerActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
*/