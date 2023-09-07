package com.example.demoar

import android.app.Activity
import android.app.ActivityManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.PixelCopy
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val TAG = "longtq"
    private val MIN_OPENGL_VERSION = 3.0
    private val numberOfNodes = 18

    private var arFragment: ArFragment? = null
    private lateinit var btnCapture: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
        setContentView(R.layout.activity_main)
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?
        btnCapture = findViewById(R.id.btnCapture)
        btnCapture.setOnClickListener {
            takePhoto()
        }
    }

    private fun generateCacheFilename(): String {
        val date: String = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val cacheDir = cacheDir
        return cacheDir.toString() + File.separator + "IM/" + date + "_screenshot.jpg"
    }

    @Throws(IOException::class)
    private fun saveBitmapToDisk(bitmap: Bitmap, filename: String) {
        val out = File(filename)
        if (out.parentFile?.exists() == false) {
            out.parentFile?.mkdirs()
        }
        try {
            FileOutputStream(filename).use { outputStream ->
                ByteArrayOutputStream().use { outputData ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData)
                    outputData.writeTo(outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
            }
        } catch (ex: IOException) {
            throw IOException("Failed to save bitmap to disk", ex)
        }
    }

    private fun takePhoto() {
        val filename = generateCacheFilename()
        val view: ArSceneView = arFragment!!.arSceneView
        val bitmap = Bitmap.createBitmap(
            view.width, view.height,
            Bitmap.Config.ARGB_8888
        )

        val handlerThread = HandlerThread("PixelCopier")
        handlerThread.start()
        PixelCopy.request(view, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename)
                    runOnUiThread {
                        addMultipleNodes()
//                        addImageToNode(filename)
                    }

                } catch (e: IOException) {
                    val toast = Toast.makeText(
                        this@MainActivity, e.toString(),
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                    return@request
                }
            } else {
                //noop
            }
            handlerThread.quitSafely()
        }, Handler(handlerThread.looper))

    }

    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        val openGlVersionString = (activity.getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later")
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        return true
    }


    private fun addMultipleNodes() {
        val sceneView = arFragment?.arSceneView?.scene

        for (i in 0 until numberOfNodes) {
            val anchorNode = AnchorNode()
            anchorNode.parent = sceneView

            val node = Node()
            node.parent = anchorNode

            val position = Vector3(
                (i % 6) * 0.2f,  // Ví dụ: Chia màn hình thành 6 cột
                (i / 6) * 0.2f,  // Ví dụ: Chia màn hình thành 3 hàng
                -1.0f  // Đặt z để nằm phía dưới màn hình
            )
            node.localPosition = position


            val imageView = ImageView(this)
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            Glide.with(this)
                .load(R.drawable.img)
                .apply(RequestOptions().centerCrop())
                .override(100, 100)
                .into(imageView)
          ViewRenderable.builder()
                    .setView(this, imageView)
                    .build()
                    .thenAccept { viewRenderer ->
                        val photoNode = Node()
                        photoNode.renderable = viewRenderer

//                        val anchorNode1 = AnchorNode()
                        anchorNode.parent = arFragment?.arSceneView?.scene
                        anchorNode.addChild(photoNode)

                    }
                    .exceptionally {
                        Log.e(TAG, "Lỗi khi tạo đối tượng hiển thị: $it")
                        null
                    }

            sceneView?.addChild(anchorNode)
        }
    }


//    private fun addImageToNode(imageUrl: String) {
//        val imageView = ImageView(this)
//        imageView.layoutParams = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        Glide.with(this)
//            .load(imageUrl)
//            .apply(RequestOptions().centerCrop())
//            .override(100, 100)
//            .into(imageView)
//
//        ViewRenderable.builder()
//            .setView(this, imageView)
//            .build()
//            .thenAccept { viewRenderer ->
//                val photoNode = Node()
//                photoNode.renderable = viewRenderer
//
//                val anchorNode = AnchorNode()
//                anchorNode.parent = arFragment?.arSceneView?.scene
//                anchorNode.addChild(photoNode)
//
//            }
//            .exceptionally {
//                Log.e(TAG, "Lỗi khi tạo đối tượng hiển thị: $it")
//                null
//            }
//    }



}