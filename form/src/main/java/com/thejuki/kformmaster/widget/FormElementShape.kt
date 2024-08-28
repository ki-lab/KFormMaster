package com.thejuki.kformmaster.widget

/**
 * Form Element Shape
 *
 * A simple class to set the radius (DP) on the element's background card
 */
class FormElementShape(var topLeft: Float = 0F, var topRight: Float = 0F, var bottomLeft: Float = 0F, var bottomRight: Float = 0F) {

    fun setTop(top: Float) {
        topLeft = top
        topRight = top
    }

    fun setBottom(bottom: Float) {
        bottomLeft = bottom
        bottomRight = bottom
    }

}