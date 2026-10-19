package com.thejuki.kformmaster.widget

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Form Element Shape Outline Provider
 *
 * Clips the content of a form element card to its [FormElementShape].
 *
 * MaterialCardView only clips its content when all four corners share the same radius, so the content of a card
 * rounded on one side only (first or last row of a section) would draw over the rounded corners. A round rect is
 * the only outline every API level can clip to: for a card rounded on one side, it is stretched past the opposite
 * edge so that its other corners fall outside the view.
 */
class FormElementShapeOutlineProvider(private val shape: FormElementShape?) : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        val density = view.resources.displayMetrics.density
        val topRadius = max(shape?.topLeft ?: 0F, shape?.topRight ?: 0F) * density
        val bottomRadius = max(shape?.bottomLeft ?: 0F, shape?.bottomRight ?: 0F) * density
        val radius = max(topRadius, bottomRadius)
        val overflow = radius.roundToInt()

        when {
            topRadius > 0 && bottomRadius > 0 -> outline.setRoundRect(0, 0, view.width, view.height, radius)
            topRadius > 0 -> outline.setRoundRect(0, 0, view.width, view.height + overflow, radius)
            bottomRadius > 0 -> outline.setRoundRect(0, -overflow, view.width, view.height, radius)
            else -> outline.setRect(0, 0, view.width, view.height)
        }
    }
}
