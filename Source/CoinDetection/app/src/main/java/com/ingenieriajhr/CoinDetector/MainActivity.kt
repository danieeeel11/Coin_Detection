package com.ingenieriajhr.CoinDetector

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.ingenieriajhr.CoinDetector.databinding.ActivityMainBinding
import com.ingenieriiajhr.jhrCameraX.BitmapResponse
import com.ingenieriiajhr.jhrCameraX.CameraJhr
import org.opencv.android.OpenCVLoader
import androidx.activity.result.contract.ActivityResultContracts.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var cameraJhr: CameraJhr
    private lateinit var openUtils: OpenUtils
    //private lateinit var btnCamera: Button

    companion object {
        private const val CAMERA_FRONT = 1
        private const val CAMERA_BACK = 0
    }

    private var currentCamera = CAMERA_FRONT
    private var isCameraStarted = false
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openUtils = OpenUtils(this)

        if (OpenCVLoader.initDebug()) {
            Log.d("OPENCV2023", "TRUE")
        } else {
            Log.d("OPENCV2023", "INCORRECTO")
        }

        // Init cameraJHR
        cameraJhr = CameraJhr(this)
        // TexView
        textView = findViewById(R.id.texto)

        /*btnCamera = findViewById(R.id.btnCamera)
        btnCamera.setOnClickListener {
            openCamera()
        }*/
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && cameraJhr.allpermissionsGranted() && !isCameraStarted) {
            startCameraJhr()
            isCameraStarted = true
        } else {
            cameraJhr.noPermissions()
        }
    }

    /*fun openCamera() {
        // Switch between front and back cameras
        currentCamera = if (currentCamera == CAMERA_BACK) CAMERA_FRONT else CAMERA_BACK
        // Restart the camera with the new camera
        //startCameraJhr()

    }*/

    private fun startCameraJhr() {
        cameraJhr.addlistenerBitmap(object : BitmapResponse {
            override fun bitmapReturn(bitmap: Bitmap?) {
                val newBitmap = openUtils.setUtil(bitmap!!)
                val totalDinero = openUtils.returnTotal()
                runOnUiThread {
                    textView.text = "TOTAL EN PESOS: $totalDinero"
                }
                if (bitmap != null) {
                    runOnUiThread {
                        binding.imgBitMap.setImageBitmap(newBitmap)
                    }
                }
            }
        })

        cameraJhr.initBitmap()



        // Selector camera LENS_FACING_FRONT = 0; LENS_FACING_BACK = 1;
        // Aspect Ratio  RATIO_4_3 = 0; RATIO_16_9 = 1; false returnImageProxy, true return bitmap
        cameraJhr.start(currentCamera, 0, binding.cameraPreview, true, false, true)
    }

}
