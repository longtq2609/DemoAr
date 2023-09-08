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
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
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
import kotlin.math.PI
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private val TAG = "longtq"
    private val MIN_OPENGL_VERSION = 3.0

    private var arFragment: ArFragment? = null
    private lateinit var btnCapture: Button
    private var nodeMidList = mutableListOf<NodeMidPosition>()
    private var nodeTopList = mutableListOf<NodeTopPosition>()
    private var nodeBottomList = mutableListOf<NodeBottomPosition>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
        setContentView(R.layout.activity_main)
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?
        btnCapture = findViewById(R.id.btnCapture)
        btnCapture.setOnClickListener {
            addMultipleNodes()
        }

        val distance = sqrt(3.0)
        val width = 2.0

        val node1 = NodeMidPosition(
            R.drawable.img1,
            Vector3(0.0f, 0.0f, -distance.toFloat()),
            Quaternion(0f, 0f, 0f, 0f)
        )
        val node2 = NodeMidPosition(
            R.drawable.img2,
            Vector3((3.0 * width / 4.0).toFloat(), 0.0f, (-distance / 2.0).toFloat()),
            Quaternion(0f, 1f, 0f, -PI.toFloat() / 3f)
        )
        val node3 = NodeMidPosition(
            R.drawable.img3,
            Vector3((3.0 * width / 4.0).toFloat(), 0.0f, (distance / 2.0).toFloat()),
            Quaternion(0f, 1f, 0f, -2 * PI.toFloat() / 3f)
        )
        val node4 = NodeMidPosition(
            R.drawable.img4,
            Vector3(0.0f, 0.0f, distance.toFloat()),
            Quaternion(0f, 1f, 0f, PI.toFloat())
        )
        val node5 = NodeMidPosition(
            R.drawable.img5,
            Vector3((-3.0 * width / 4.0).toFloat(), 0.0f, (distance / 2.0).toFloat()),
            Quaternion(0f, 1f, 0f, 2 * PI.toFloat() / 3f)
        )
        val node6 = NodeMidPosition(
            R.drawable.img6,
            Vector3((-3.0 * width / 4.0).toFloat(), 0.0f, (-distance / 2.0).toFloat()),
            Quaternion(0f, 1f, 0f, PI.toFloat() / 3f)
        )

        val topY = width / 2 + sqrt(2.0) / 2

        val nodeTop1 = NodeTopPosition(
            R.drawable.img1,
            Vector3(0f, topY.toFloat(), (-distance / 2).toFloat()),
            Quaternion(PI.toFloat() / 4, 0f, 0f, 0f)
        )
        val nodeTop2 = NodeTopPosition(
            R.drawable.img1,
            Vector3((3f * width / 4f).toFloat(), topY.toFloat(), (-distance / 4).toFloat()),
            Quaternion(PI.toFloat() / 4, -PI.toFloat() / 3, 0f, 0f)
        )
        val nodeTop3 = NodeTopPosition(
            R.drawable.img1,
            Vector3((3f * width / 4f).toFloat(), topY.toFloat(), (distance / 4).toFloat()),
            Quaternion(PI.toFloat() / 4, -2 * PI.toFloat() / 3, 0f, 0f)
        )
        val nodeTop4 = NodeTopPosition(
            R.drawable.img1,
            Vector3(0f, topY.toFloat(), (distance / 2).toFloat()),
            Quaternion(PI.toFloat() / 4, PI.toFloat(), 0f, 0f)
        )
        val nodeTop5 = NodeTopPosition(
            R.drawable.img1,
            Vector3(((-3f * width / 4f)).toFloat(), topY.toFloat(), (distance / 4).toFloat()),
            Quaternion(PI.toFloat() / 4, 2 * PI.toFloat() / 3, 0f, 0f)
        )
        val nodeTop6 = NodeTopPosition(
            R.drawable.img1,
            Vector3((-3f * width / 4f).toFloat(), topY.toFloat(), (-distance / 4).toFloat()),
            Quaternion(PI.toFloat() / 4, PI.toFloat() / 3, 0f, 0f)
        )






        nodeMidList.add(node1)
        nodeMidList.add(node2)
        nodeMidList.add(node3)
        nodeMidList.add(node4)
        nodeMidList.add(node5)
        nodeMidList.add(node6)

        nodeTopList.add(nodeTop1)
        nodeTopList.add(nodeTop2)
        nodeTopList.add(nodeTop3)
        nodeTopList.add(nodeTop4)
        nodeTopList.add(nodeTop5)
        nodeTopList.add(nodeTop6)
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
        nodeMidList.forEach { nodePosition ->
            val photoNode = Node()
            val imageView = ImageView(this)
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            Glide.with(this)
                .load(nodePosition.pathFile)
                .apply(RequestOptions().centerCrop())
                .override(500, 500)
                .into(imageView)

            ViewRenderable.builder()
                .setView(this, imageView)
                .build()
                .thenAccept { viewRenderable ->
                    photoNode.renderable = viewRenderable
                    photoNode.localPosition = nodePosition.position
                    photoNode.localRotation = nodePosition.rotations

                    val anchorNode = AnchorNode()
                    anchorNode.parent = sceneView
                    anchorNode.addChild(photoNode)
                }
                .exceptionally {
                    Log.e(TAG, "Lỗi khi tạo đối tượng hiển thị: $it")
                    null
                }
        }
        nodeTopList.forEach { nodePosition ->
            val photoNode = Node()
            val imageView = ImageView(this)
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            Glide.with(this)
                .load(nodePosition.pathFile)
                .apply(RequestOptions().centerCrop())
                .override(500, 500)
                .into(imageView)

            ViewRenderable.builder()
                .setView(this, imageView)
                .build()
                .thenAccept { viewRenderable ->
                    photoNode.renderable = viewRenderable
                    photoNode.localPosition = nodePosition.position
                    photoNode.localRotation = nodePosition.rotations

                    val anchorNode = AnchorNode()
                    anchorNode.parent = sceneView
                    anchorNode.addChild(photoNode)
                }
                .exceptionally {
                    Log.e(TAG, "Lỗi khi tạo đối tượng hiển thị: $it")
                    null
                }
        }

    }


}