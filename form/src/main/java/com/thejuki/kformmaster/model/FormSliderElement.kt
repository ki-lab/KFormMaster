package com.thejuki.kformmaster.model

import androidx.appcompat.widget.AppCompatSeekBar
import kotlin.math.pow

/**
 * Form Slider Element
 *
 * Form element for AppCompatSeekBar
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
open class FormSliderElement(tag: Int = -1) : BaseFormElement<Float>(tag) {

    override fun clear() {
        this.value = null
    }

    var decimalsCount = 0

    val multiplicator
        get() = 10f.pow(decimalsCount)

    /**
     * Maximum progress of the slider
     */
    var max: Float = 100f
        get() {
            return if (field <= 0) 100f else field
        }

    /**
     * Minimum progress of the slider
     */
    var min: Float = 0f
        get() {
            return if (field <= 0f) 0f else field
        }


    val minValueString
        get() = "%.${decimalsCount}f".format(min)
    val maxValueString
        get() = "%.${decimalsCount}f".format(max)

    var minLabel = ""
    var maxLabel = ""

    /**
     * Increments of the slider
     * NOTE: steps must be null. Use steps or incrementBy, not both.
     * Ex. An increment by of 5 with max of 100 would increment by 5's: 0, 5, 10, 15, 20, 25,... to 100
     */
    var incrementBy: Float? = null
        get() {
            return if (field != null && field ?: 0f <= 0f) 1f else field
        }

    /**
     * Steps of the slider
     * NOTE: incrementBy must be null. Use steps or incrementBy, not both.
     * Ex. 20 steps with max of 100 would step 0, 5, 10, 15, 20, 25,... to 100
     */
    var steps: Int? = null
        get() {
            return if (field != null && field ?: 0 <= 0) 1 else field
        }

    val sliderMin: Int
        get() = (min * multiplicator).toInt()

    val sliderMax: Int
        get() = (max * multiplicator).toInt()

    val sliderLength: Int
        get() = sliderMax - sliderMin

    val sliderIncrementBy: Int?
        get() =  incrementBy?.times(multiplicator)?.toInt().let { if( it == null || it > 0)it else 1 }

    val sliderValue
        get() = (value?.times(multiplicator)?:min).toInt() - sliderMin

    override fun displayNewValue() {
        editView?.let {
            if (it is AppCompatSeekBar) {
                it.progress = sliderValue
            }
        }
    }
}