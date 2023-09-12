package com.example.demoar

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.FixedHeightViewSizer
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import kotlin.math.PI
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "longtq"
        const val MIN_OPENGL_VERSION = 3.0
    }

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
            addNode()
        }
    }

//    private fun generateCacheFilename(): String {
//        val date: String = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
//        val cacheDir = cacheDir
//        return cacheDir.toString() + File.separator + "IM/" + date + "_screenshot.jpg"
//    }
//
//    @Throws(IOException::class)
//    private fun saveBitmapToDisk(bitmap: Bitmap, filename: String) {
//        val out = File(filename)
//        if (out.parentFile?.exists() == false) {
//            out.parentFile?.mkdirs()
//        }
//        try {
//            FileOutputStream(filename).use { outputStream ->
//                ByteArrayOutputStream().use { outputData ->
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData)
//                    outputData.writeTo(outputStream)
//                    outputStream.flush()
//                    outputStream.close()
//                }
//            }
//        } catch (ex: IOException) {
//            throw IOException("Failed to save bitmap to disk", ex)
//        }
//    }
//
//    private fun takePhoto() {
//        val filename = generateCacheFilename()
//        val view: ArSceneView = arFragment!!.arSceneView
//        val bitmap = Bitmap.createBitmap(
//            view.width, view.height,
//            Bitmap.Config.ARGB_8888
//        )
//
//        val handlerThread = HandlerThread("PixelCopier")
//        handlerThread.start()
//        PixelCopy.request(view, bitmap, { copyResult ->
//            if (copyResult == PixelCopy.SUCCESS) {
//                try {
//                    saveBitmapToDisk(bitmap, filename)
//                    runOnUiThread {
////                        addImageToNode(filename)
//                    }
//
//                } catch (e: IOException) {
//                    val toast = Toast.makeText(
//                        this@MainActivity, e.toString(),
//                        Toast.LENGTH_LONG
//                    )
//                    toast.show()
//                    return@request
//                }
//            } else {
//                //noop
//            }
//            handlerThread.quitSafely()
//        }, Handler(handlerThread.looper))
//
//    }

    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        val openGlVersionString = (activity.getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later")
            activity.finish()
            return false
        }
        return true
    }


    private fun addNode() {

        val distance = sqrt(3.0)
        val width = 2.0

        val positions = listOf(
            Vector3(0.0f, 0f, -distance.toFloat()),
            Vector3((3.0 * width / 4.0).toFloat(), 0f, (-distance / 2.0).toFloat()),
            Vector3((3.0 * width / 4.0).toFloat(), 0f, (distance / 2.0).toFloat()),
            Vector3(0.0f, 0f, distance.toFloat()),
            Vector3((-3.0 * width / 4.0).toFloat(), 0f, (distance / 2.0).toFloat()),
            Vector3((-3.0 * width / 4.0).toFloat(), 0f, (-distance / 2.0).toFloat()),
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
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
            R.drawable.img5,
            R.drawable.img6
        )

        for (i in 0 until 6) {

            val imageView = ImageView(this)
            Glide.with(this)
                .load(imageResourcesMid[i % imageResourcesMid.size])
                .apply(RequestOptions().centerCrop())
                .into(imageView)


            ViewRenderable.builder()
                .setView(this, imageView)
                .setSizer(FixedHeightViewSizer(distance.toFloat()))
                .build()
                .thenAccept { viewRenderer ->
                    val anchorNode = Node()
                    anchorNode.name = "mid_node$i"
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

        val topY = (width / 2 + sqrt(2.0) / 2).toFloat()


        val topPositions = listOf(
            Vector3(0.0f, topY, -distance.toFloat()),
            Vector3((3.0 * width / 4.0).toFloat(), topY, (-distance / 2.0).toFloat()),
            Vector3((3.0 * width / 4.0).toFloat(), topY, (distance / 2.0).toFloat()),
            Vector3(0.0f, topY, distance.toFloat()),
            Vector3((-3.0 * width / 4.0).toFloat(), topY, (distance / 2.0).toFloat()),
            Vector3((-3.0 * width / 4.0).toFloat(), topY, (-distance / 2.0).toFloat()),
        )

        val topRotations = listOf(
            Quaternion(0f, 1f, PI.toFloat() / 4f, 0f),
            Quaternion(0f, 1f, -PI.toFloat() / 4f, -PI.toFloat() / 2f),
            Quaternion(-PI.toFloat() / 5f, 1f, -PI.toFloat() / 4f, -2 * PI.toFloat() / 8f),
            Quaternion(0f, 1f, -PI.toFloat() / 4f, 0f),
            Quaternion(0f, 1f, -PI.toFloat() / 4f, 2 * PI.toFloat() / 8f),
            Quaternion(0f, 1f, -PI.toFloat() / 4f, PI.toFloat() / 2f),
        )

        for (i in 0 until 6) {

            val imageView = ImageView(this)
            Glide.with(this)
                .load(imageResourcesMid[i % imageResourcesMid.size])
                .apply(RequestOptions().centerCrop())
                .into(imageView)


            ViewRenderable.builder()
                .setView(this, imageView)
                .setSizer(FixedHeightViewSizer(distance.toFloat()))
                .build()
                .thenAccept { viewRenderer ->
                    val anchorNode = Node()
                    anchorNode.name = "top_node$i"
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

        val bottomY = -(width / 2 + sqrt(2.0) / 2).toFloat()
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
                .into(imageView)


            ViewRenderable.builder()
                .setView(this, imageView)
                .setSizer(FixedHeightViewSizer(distance.toFloat()))
                .build()
                .thenAccept { viewRenderer ->
                    val anchorNode = Node()
                    anchorNode.name = "bottom_node$i"
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
}