package com.example.demoar

import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3

data class NodeMidPosition(
    var pathFile: Int,
    var position: Vector3,
    var rotations: Quaternion,
)

data class NodeTopPosition(
    var pathFile: Int,
    var position: Vector3,
    var rotations: Quaternion,
)

data class NodeBottomPosition(
    var pathFile: Int,
    var position: Vector3,
    var rotations: Quaternion,
)

