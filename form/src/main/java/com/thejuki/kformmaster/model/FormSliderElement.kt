package com.thejuki.kformmaster.model

import androidx.appcompat.widget.AppCompatTextView

/**
 * Form Slider Element
 *
 * Form element for AppCompatSeekBar
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
open class FormSliderElement<T: Number>(tag: Int = -1) : BaseFormElement<T>(tag) {

    override fun clear() {
        this.value = min
    }

    /**
     * Maximum progress of the slider
     */
    var max: T = (100 as T)
        get() {
            return if (field.toDouble() <= 0.toDouble()) (100 as T) else field
        }
    /**
     * Maximum progress of the slider label
     */
    var maxLabel: String = ""


    /**
     * Minimum progress of the slider
     */
    var min: T = (0 as T)
        get() {
            return if (field.toDouble() <= 0.toDouble()) (0 as T) else field
        }

    /**
     * Minimum progress of the slider label
     */
    var minLabel: String = ""

    /**
     * Increments of the slider
     * NOTE: steps must be null. Use steps or incrementBy, not both.
     * Ex. An increment by of 5 with max of 100 would increment by 5's: 0, 5, 10, 15, 20, 25,... to 100
     */
    var incrementBy: T? = null
        get() {
            return if (field?.toDouble() != null && field?.toDouble() ?: 0.toDouble() <= 0.toDouble()) (1 as T) else field
        }

    /**
     * Steps of the slider
     * NOTE: incrementBy must be null. Use steps or incrementBy, not both.
     * Ex. 20 steps with max of 100 would step 0, 5, 10, 15, 20, 25,... to 100
     */
    var steps: T? = null
        get() {
            return if (field?.toDouble() != null && field?.toDouble() ?: 0.toDouble() <= 0.toDouble()) (1 as T) else field
        }

    var decimalNumberFormat : String = "%.1f"

    override fun displayNewValue() {
        editView?.let {
            if (it is AppCompatTextView) {
                it.text = valueToString()
            }
        }
    }

    private fun valueToString(): String =
            when(value){
                is Float -> decimalNumberFormat.format(value)
                is Double -> decimalNumberFormat.format(value)
                is Int -> value?.toInt().toString()
                else -> value.toString()
            }
}