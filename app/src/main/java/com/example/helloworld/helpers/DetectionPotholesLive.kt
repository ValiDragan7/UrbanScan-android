package com.example.helloworld
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import android.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.helloworld.ml.ModelPotholes
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import androidx.fragment.app.Fragment

class DetectionPotholesLive : AppCompatActivity(){

    var colors = listOf<Int>(
        Color.BLUE,Color.GREEN,Color.RED,Color.CYAN,Color.GRAY,Color.BLACK,
        Color.DKGRAY,Color.MAGENTA,Color.YELLOW,Color.RED
    )
    val paint = Paint()
    lateinit var imageProcessor: ImageProcessor
    lateinit var bitmap: Bitmap
    lateinit var imageView: ImageView
    lateinit var cameraDevice :CameraDevice
    lateinit var handler: Handler
    lateinit var cameraManager: CameraManager
    lateinit var textureView: TextureView
    lateinit var model: ModelPotholes
    lateinit var mediaPlayer: MediaPlayer
    private companion object{
        private const val TAG = "PERMISSION_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detection_potholes_live)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        get_permission()
        imageProcessor = ImageProcessor.Builder().add(ResizeOp(320,320,ResizeOp.ResizeMethod.BILINEAR)).build()
        model = ModelPotholes.newInstance(this)
        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        imageView = findViewById(R.id.imageView)

        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                open_camera()
            }
            fun isMediaPlayerInitialized(): Boolean {
                return ::mediaPlayer.isInitialized
            }
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(pO: SurfaceTexture) {
                bitmap = textureView.bitmap!!

                var image = TensorImage.fromBitmap(bitmap)

                val outputs = model.process(image)
                val detectionResultsList = outputs.detectionResultList

                val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutable)
                if (detectionResultsList.isNotEmpty()){
                    if (!::mediaPlayer.isInitialized){
                        mediaPlayer = MediaPlayer.create(this@DetectionPotholesLive, R.raw.alert)
                    }
//

                    for (detectionResults in detectionResultsList) {
                        val location = detectionResults.locationAsRectF
                        val score = detectionResults.scoreAsFloat

                        if (score > 0.5) { // Adjust the confidence threshold as needed
                            mediaPlayer.start()
                            val color = colors.get(1) // Assuming colors is a list of colors

                            paint.color = color
                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = 3f // Adjust stroke width as needed

                            canvas.drawRect(location, paint)

                        }
                    }
                }

                imageView.setImageBitmap(mutable)
            }

        }

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    override fun onDestroy() {
        if (::mediaPlayer.isInitialized){
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        super.onDestroy()
        model.close()
    }

    @SuppressLint("MissingPermission")
    fun open_camera(){
        cameraManager.openCamera(cameraManager.cameraIdList[0], object: CameraDevice.StateCallback(){
            override fun onOpened(pO: CameraDevice) {
                cameraDevice = pO

                var surfaceTexture = textureView.surfaceTexture
                var surface = Surface(surfaceTexture)

                var captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface),object: CameraCaptureSession.StateCallback(){
                    override fun onConfigured(pO: CameraCaptureSession) {
                        pO.setRepeatingRequest(captureRequest.build(),null,null)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                    }


                },handler)
            }

            override fun onDisconnected(camera: CameraDevice) {

            }

            override fun onError(camera: CameraDevice, error: Int) {

            }

        },handler)
    }

    fun onGoToImageActivity(view: View) {
        val context = view.context
        val intent = Intent(context, DetectionPotholesLive::class.java)
        context.startActivity(intent)
    }

    fun get_permission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            get_permission()
        }
    }
}
