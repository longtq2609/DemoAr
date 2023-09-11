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
import com.google.ar.sceneform.AnchorNode
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


//    private fun addMultipleNodes() {
//        val sceneView = arFragment?.arSceneView?.scene
//        val distance = sqrt(3.0)
//        val width = 2.0
//
//        val position = arrayOf(
//            Vector3(0.0f, -1.0f, -distance.toFloat()),
//            Vector3((3.0 * width / 4.0).toFloat(), -1.0f, (-distance / 2.0).toFloat()),
//            Vector3((3.0 * width / 4.0).toFloat(), -1.0f, (distance / 2.0).toFloat()),
//            Vector3(0.0f, -1.0f, distance.toFloat()),
//            Vector3((-3.0 * width / 4.0).toFloat(), -1.0f, (distance / 2.0).toFloat()),
//            Vector3((-3.0 * width / 4.0).toFloat(), -1.0f, (-distance / 2.0).toFloat()),
//        )
//
//        val rotations = arrayOf(
//            Quaternion(0f, 1f, 0f, 0f),
//            Quaternion(0f, 1f, 0f, -PI.toFloat() / 2f),
//            Quaternion(0f, 1f, 0f, -2 * PI.toFloat() / 8f),
//            Quaternion(0f, 1f, 0f, 0f),
//            Quaternion(0f, 1f, 0f, 2 * PI.toFloat() / 8f),
//            Quaternion(0f, 1f, 0f, PI.toFloat() / 2f),
//        )
//
//        val imageResourcesMid = arrayOf(
//            R.drawable.img1,
//            R.drawable.img2,
//            R.drawable.img3,
//            R.drawable.img4,
//            R.drawable.img5,
//            R.drawable.img6
//        )
//        val imageResourcesTop = arrayOf(
//            R.drawable.img1,
//            R.drawable.img2,
//            R.drawable.img3,
//            R.drawable.img4,
//            R.drawable.img5,
//            R.drawable.img6
//        )
//        val imageResourcesBottom = arrayOf(
//            R.drawable.img1,
//            R.drawable.img2,
//            R.drawable.img3,
//            R.drawable.img4,
//            R.drawable.img5,
//            R.drawable.img6
//        )
//
//        val topY = (width / 2 + sqrt(2.0) / 2).toFloat()
//        val topPositions = listOf(
//            Vector3(0f, topY, (-distance / 2).toFloat()),
//            Vector3((3 * width / 4).toFloat(), topY, (-distance / 4).toFloat()),
//            Vector3((3 * width / 4).toFloat(), topY, (distance / 4).toFloat()),
//            Vector3(0f, topY, (distance / 2).toFloat()),
//            Vector3((-3 * width / 4).toFloat(), topY, (distance / 4).toFloat()),
//        )
//
//        val topRotations = listOf(
//            Quaternion(Vector3(Math.PI.toFloat() / 4, 0f, 0f)),
//            Quaternion(Vector3(Math.PI.toFloat() / 4, (-Math.PI / 3).toFloat(), 0f)),
//            Quaternion(Vector3(Math.PI.toFloat() / 4, (-2 * Math.PI / 3).toFloat(), 0f)),
//            Quaternion(Vector3(Math.PI.toFloat() / 4, Math.PI.toFloat(), 0f)),
//            Quaternion(Vector3(Math.PI.toFloat() / 4, (2 * Math.PI / 3).toFloat(), 0f)),
//            Quaternion(Vector3(Math.PI.toFloat() / 4, (Math.PI / 3).toFloat(), 0f))
//        )
//        for (i in 0 until 6) {
//
//            val photoNode = Node()
//            val imageView = ImageView(this)
//            imageView.layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//
//            Glide.with(this)
//                .load(imageResourcesMid[i % imageResourcesMid.size])
//                .apply(RequestOptions().centerCrop())
//                .override(650, 650)
//                .into(imageView)
//
//            ViewRenderable.builder()
//                .setView(this, imageView)
//                .build()
//                .thenAccept { viewRenderer ->
//                    photoNode.renderable = viewRenderer
//                    photoNode.localPosition = position[i % position.size]
//                    photoNode.localRotation = rotations[i % rotations.size]
//                    val anchorNode = AnchorNode()
//                    anchorNode.parent = sceneView
//                    anchorNode.addChild(photoNode)
//                }
//                .exceptionally {
//                    Log.e(TAG, "$it")
//                    null
//                }
//        }
//
//        for (i in 0 until 6) {
//
//            val photoNode = Node()
//            val imageView = ImageView(this)
//            imageView.layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//
//            Glide.with(this)
//                .load(imageResourcesTop[i % imageResourcesTop.size])
//                .apply(RequestOptions().centerCrop())
//                .override(650, 650)
//                .into(imageView)
//
//            ViewRenderable.builder()
//                .setView(this, imageView)
//                .build()
//                .thenAccept { viewRenderer ->
//                    photoNode.renderable = viewRenderer
//                    photoNode.localPosition = topPositions[i % topPositions.size]
//                    photoNode.localRotation = topRotations[i % topRotations.size]
//                    val anchorNode = AnchorNode()
//                    anchorNode.parent = sceneView
//                    anchorNode.addChild(photoNode)
//                }
//                .exceptionally {
//                    Log.e(TAG, "$it")
//                    null
//                }
//        }
//
//
//    }

    private fun addNode() {

        val distance = sqrt(3.0)
        val width = 2.0

        val positions = listOf(
            Vector3(0f, -1f, (-distance).toFloat()),
            Vector3((3 * width / 4).toFloat(), -1f, (-distance / 2).toFloat()),
            Vector3((3 * width / 4).toFloat(), -1f, (distance / 2).toFloat()),
            Vector3(0f, -1f, distance.toFloat()),
            Vector3((-3 * width / 4).toFloat(), -1f, (distance / 2).toFloat()),
            Vector3((-3 * width / 4).toFloat(), -1f, (-distance / 2).toFloat())
        )

        val rotations = listOf(
            Quaternion(0f, 1f, 0f, 0f),
            Quaternion(0f, 1f, 0f, -PI.toFloat() / 3),
            Quaternion(0f, 1f, 0f, -2 * PI.toFloat() / 3),
            Quaternion(0f, 1f, 0f, PI.toFloat()),
            Quaternion(0f, 1f, 0f, 2 * PI.toFloat() / 3),
            Quaternion(0f, 1f, 0f, PI.toFloat() / -5)
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
                    val anchorNode = AnchorNode()
                    anchorNode.renderable = viewRenderer
                    anchorNode.parent = arFragment?.arSceneView?.scene
                    anchorNode.localPosition = positions[i]
                    anchorNode.localRotation = rotations[i]
                    arFragment?.arSceneView?.scene?.addChild(anchorNode)
                }
                .exceptionally {
                    Log.e(TAG, "$it")
                    null
                }
        }
//        val topY = (width / 2 + sqrt(2.0) / 2).toFloat()
//        val topPositions = listOf(
//            Vector3(0f, topY, (-distance / 2).toFloat()),
//            Vector3((3 * width / 4).toFloat(), topY, (-distance / 4).toFloat()),
//            Vector3((3 * width / 4).toFloat(), topY, (distance / 4).toFloat()),
//            Vector3(0f, topY, (distance / 2).toFloat()),
//            Vector3((-3 * width / 4).toFloat(), topY, (distance / 4).toFloat()),
//            Vector3((-3 * width / 4).toFloat(), topY, (-distance / 4).toFloat())
//        )
//
//        val topRotations = listOf(
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), (Math.PI / 4).toFloat()),
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), (Math.PI / 4).toFloat()),
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), (Math.PI / 4).toFloat()),
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), Math.PI.toFloat()),
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), (-Math.PI / 4).toFloat()),
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), (-Math.PI / 3).toFloat())
//        )
//
//        for (i in 0 until 6) {
//
//
//            val planeNode = Node()
//            planeNode.name = "top_node$i"
//            planeNode.localPosition = topPositions[i]
//            sceneView?.addChild(planeNode)
//            planeNode.localRotation = topRotations[i]
//
//
//        }

//        val bottomY = -(width / 2 + sqrt(2.0) / 2).toFloat()
//        val bottomPositions = listOf(
//            Vector3(0f, bottomY, (-distance / 2).toFloat()),
//            Vector3((3 * width / 4).toFloat(), bottomY, (-distance / 4).toFloat()),
//            Vector3((3 * width / 4).toFloat(), bottomY, (distance / 4).toFloat()),
//            Vector3(0f, bottomY, (distance / 2).toFloat()),
//            Vector3((-3 * width / 4).toFloat(), bottomY, (distance / 4).toFloat()),
//            Vector3((-3 * width / 4).toFloat(), bottomY, (-distance / 4).toFloat())
//        )
//
//        val bottomRotations = listOf(
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), (-Math.PI / 4).toFloat()),
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), (-Math.PI / 4).toFloat()),
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), (-Math.PI / 4).toFloat()),
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), Math.PI.toFloat()),
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), (Math.PI / 4).toFloat()),
//            Quaternion.axisAngle(Vector3(1f, 0f, 0f), (Math.PI / 3).toFloat())
//        )
//
//        for (i in 0 until 6) {
//            val planeNode = Node()
//            planeNode.name = "bottom_node$i"
//            planeNode.localPosition = bottomPositions[i]
//            sceneView?.addChild(planeNode)
//            planeNode.localRotation = bottomRotations[i]
//
//        }
    }
}