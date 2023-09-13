package com.example.demoar

import android.app.Activity
import android.app.ActivityManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.PixelCopy
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.FixedHeightViewSizer
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private var arFragment: ArFragment? = null
    private lateinit var btnCapture: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
        setContentView(R.layout.activity_main)
        init()
        listener()
    }

    private fun listener() {
        btnCapture.setOnClickListener {
            takePhoto()
        }
    }

    private fun init() {
        btnCapture = findViewById(R.id.btnCapture)
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?
        addNode()
    }

    private fun generateCacheFilename(): String {
        val date: String = SimpleDateFormat(FORMAT_TIME, Locale.getDefault()).format(Date())
        val cacheDir = cacheDir
        return cacheDir.toString() + File.separator + DOMAIN_URI + date + NAME_SCREEN
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
            Log.e(TAG, "Scene-form requires OpenGL ES 3.0 later")
            activity.finish()
            return false
        }
        return true
    }


    private fun addNode() {

        val distance = sqrt(3.0)
        val width = 2.0

        val positions = listOf(
            Vector3(0.0f, -1f, -distance.toFloat()),
            Vector3((3.0 * width / 4.0).toFloat(), -1f, (-distance / 2.0).toFloat()),
            Vector3((3.0 * width / 4.0).toFloat(), -1f, (distance / 2.0).toFloat()),
            Vector3(0.0f, -1f, distance.toFloat()),
            Vector3((-3.0 * width / 4.0).toFloat(), -1f, (distance / 2.0).toFloat()),
            Vector3((-3.0 * width / 4.0).toFloat(), -1f, (-distance / 2.0).toFloat()),
        )

        val rotations = listOf(
            Quaternion(0f, 1f, 0f, 0f),
            Quaternion(0f, 1f, 0f, -PI.toFloat() / 2f),
            Quaternion(0f, 1f, 0f, -2 * PI.toFloat() / 8f),
            Quaternion(0f, 1f, 0f, 0f),
            Quaternion(0f, 1f, 0f, 2 * PI.toFloat() / 8f),
            Quaternion(0f, 1f, 0f, PI.toFloat() / 2f),
        )

        val imageResourcesMid = arrayOf(
            R.drawable.img_2,
            R.drawable.img_2,
            R.drawable.img_2,
            R.drawable.img_2,
            R.drawable.img_2,
            R.drawable.img_2
        )

        for (i in 0 until 6) {

            val imageView = ImageView(this)
            Glide.with(this)
                .load(imageResourcesMid[i % imageResourcesMid.size])
                .apply(RequestOptions().centerCrop())
                .override(250, 250)
                .into(imageView)



            ViewRenderable.builder()
                .setView(this, imageView)
                .setSizer(FixedHeightViewSizer(distance.toFloat()))
                .build()
                .thenAccept { viewRenderer ->
                    val anchorNode = Node()
                    anchorNode.name = "mid_node_$i"
                    anchorNode.localPosition = positions[i]
                    arFragment?.arSceneView?.scene?.addChild(anchorNode)
                    anchorNode.localRotation = rotations[i]
                    anchorNode.renderable = viewRenderer
                }
                .exceptionally {
                    Log.e(TAG, "$it")
                    null
                }
        }

        val topPositions = listOf(
            Vector3(0.0f, 1f, -distance.toFloat()),
            Vector3((3.0 * width / 4.0).toFloat(), 1f, (-distance / 2.0).toFloat()),
            Vector3((3.0 * width / 4.0).toFloat(), 1f, (distance / 2.0).toFloat()),
            Vector3(0.0f, 1f, distance.toFloat()),
            Vector3((-3.0 * width / 4.0).toFloat(), 1f, (distance / 2.0).toFloat()),
            Vector3((-3.0 * width / 4.0).toFloat(), 1f, (-distance / 2.0).toFloat()),
        )

        val topRotations = listOf(
            Quaternion(0f, 1f, PI.toFloat() / 4f, 0f),
            Quaternion(-PI.toFloat() / 3f, 1f, -PI.toFloat() / 4f, -PI.toFloat() / 2f),
            Quaternion(-PI.toFloat() / 6f, 1f, -PI.toFloat() / 4f, -2 * PI.toFloat() / 8f),
            Quaternion(0f, 1f, -PI.toFloat() / 4f, 0f),
            Quaternion(PI.toFloat() / 6f, 1f, -PI.toFloat() / 4f, 2 * PI.toFloat() / 8f),
            Quaternion(PI.toFloat() / 2f, 1f, -PI.toFloat() / 4f, PI.toFloat() / 2f),
        )

        for (i in 0 until 6) {

            val imageView = ImageView(this)
            Glide.with(this)
                .load(imageResourcesMid[i % imageResourcesMid.size])
                .apply(RequestOptions().centerCrop())
                .override(250, 250)
                .into(imageView)

            ViewRenderable.builder()
                .setView(this, imageView)
                .setSizer(FixedHeightViewSizer(distance.toFloat()))
                .build()
                .thenAccept { viewRenderer ->
                    val anchorNode = Node()
                    anchorNode.name = "top_node_$i"
                    anchorNode.localPosition = topPositions[i]
                    arFragment?.arSceneView?.scene?.addChild(anchorNode)
                    anchorNode.localRotation = topRotations[i]
                    anchorNode.renderable = viewRenderer

                }
                .exceptionally {
                    Log.e(TAG, "$it")
                    null
                }


        }

        val bottomY = -3f
        val bottomPositions = listOf(
            Vector3(0.0f, bottomY, -distance.toFloat()),
            Vector3((3.0 * width / 4.0).toFloat(), bottomY, (-distance / 2.0).toFloat()),
            Vector3((3.0 * width / 4.0).toFloat(), bottomY, (distance / 2.0).toFloat()),
            Vector3(0.0f, bottomY, distance.toFloat()),
            Vector3((-3.0 * width / 4.0).toFloat(), bottomY, (distance / 2.0).toFloat()),
            Vector3((-3.0 * width / 4.0).toFloat(), bottomY, (-distance / 2.0).toFloat()),
        )

        val bottomRotations = listOf(
            Quaternion(0f, 1f, 0f, 0f),
            Quaternion(0f, 1f, 0f, -PI.toFloat() / 2f),
            Quaternion(0f, 1f, 0f, -2 * PI.toFloat() / 8f),
            Quaternion(0f, 1f, 0f, 0f),
            Quaternion(0f, 1f, 0f, 2 * PI.toFloat() / 8f),
            Quaternion(0f, 1f, 0f, PI.toFloat() / 2f),
        )

        for (i in 0 until 6) {
            val imageView = ImageView(this)
            Glide.with(this)
                .load(imageResourcesMid[i % imageResourcesMid.size])
                .apply(RequestOptions().centerCrop())
                .override(250, 250)
                .into(imageView)

            ViewRenderable.builder()
                .setView(this, imageView)
                .setSizer(FixedHeightViewSizer(distance.toFloat()))
                .build()
                .thenAccept { viewRenderer ->
                    val anchorNode = Node()
                    anchorNode.name = "bottom_node_$i"
                    anchorNode.localPosition = bottomPositions[i]
                    arFragment?.arSceneView?.scene?.addChild(anchorNode)
                    anchorNode.localRotation = bottomRotations[i]
                    anchorNode.renderable = viewRenderer
                }
                .exceptionally {
                    Log.e(TAG, "$it")
                    null
                }

        }
    }

    private companion object {
        const val TAG = "longtq"
        const val MIN_OPENGL_VERSION = 3.0
        const val FORMAT_TIME = "yyyyMMddHHmm_ss"
        const val NAME_SCREEN = "_screenshot.jpg"
        const val DOMAIN_URI = "IM/"
    }
}