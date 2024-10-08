package com.thejuki.kformmaster.view

import android.view.View
import android.widget.SeekBar
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.google.android.material.card.MaterialCardView
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.model.FormSliderElement
import kotlin.math.roundToInt

/**
 * Form Slider Binder
 *
 * View Binder for [FormSliderElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormSliderViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewRenderer() {
    val viewRenderer = ViewRenderer(layoutID
            ?: R.layout.form_element_slider, FormSliderElement::class.java) { model, finder: FormViewFinder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? MaterialCardView
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val textViewMinValue = finder.find(R.id.formElementMinValue) as? AppCompatTextView
        val textViewMaxValue = finder.find(R.id.formElementMaxValue) as? AppCompatTextView
        val textViewMinLabel = finder.find(R.id.formElementMinLabel) as? AppCompatTextView
        val textViewMaxLabel = finder.find(R.id.formElementMaxLabel) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val clearSlider = finder.find(R.id.clearSlider) as? AppCompatImageView
        val itemView = finder.getRootView() as View
        val slider = finder.find(R.id.formElementValue) as AppCompatSeekBar
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout, slider, clearSlider)

        val progressValue = finder.find(R.id.formElementProgress) as AppCompatTextView

        textViewMinValue?.text = model.minValueString
        textViewMaxValue?.text = model.maxValueString
        textViewMinLabel?.text = model.minLabel
        textViewMaxLabel?.text = model.maxLabel

        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
            tip?.visibility = View.VISIBLE
        }


        slider.progress = model.sliderValue
        slider.max = model.sliderLength

        if(model.value == null) {
            progressValue.visibility = View.INVISIBLE
            clearSlider?.visibility = View.INVISIBLE
        }
        progressValue.text = model.valueAsString

        clearSlider?.setOnClickListener {
            model.clear()
            progressValue.visibility = View.INVISIBLE
            clearSlider.visibility = View.INVISIBLE
        }

        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSeekValue(model, progress, progressValue, clearSlider)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        itemView.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()
        }
    }

    private fun updateSeekValue(model: FormSliderElement, sliderValue: Int, progressValue: AppCompatTextView, clearSlider: AppCompatImageView?) {
        if(model.value != null || sliderValue != 0) {
            var roundedValue = 0
            val minimumValue = 0
            val maximumValue = model.sliderLength

            if (model.steps != null) {
                model.steps?.let {
                    val steps: Double = it.toDouble()
                    val stepValue: Int = ((sliderValue - minimumValue) / (maximumValue - minimumValue) * steps).roundToInt()
                    val stepAmount: Int = ((maximumValue - minimumValue) / steps).roundToInt()
                    roundedValue = stepValue * stepAmount + model.sliderMin
                }
            } else if (model.steps == null && model.sliderIncrementBy != null) {
                model.sliderIncrementBy?.let {
                    val offset = minimumValue % it
                    val stepValue: Int = ((sliderValue - offset) / it)
                    roundedValue = stepValue * it + offset
                }
            } else if (model.steps == null && model.incrementBy == null) {
                roundedValue = sliderValue
            }

            roundedValue += model.sliderMin

            if (roundedValue < model.sliderMin) {
                roundedValue = model.sliderMin
            } else if (roundedValue > model.sliderMax) {
                roundedValue = model.sliderMax
            }

            model.error = null
            model.setValue(roundedValue / model.multiplicator)
        }

        formBuilder.onValueChanged(model)

        if(model.value != null) {
            progressValue.visibility = View.VISIBLE
            clearSlider?.visibility = View.VISIBLE
            progressValue.text = model.valueAsString
        }
    }
}
