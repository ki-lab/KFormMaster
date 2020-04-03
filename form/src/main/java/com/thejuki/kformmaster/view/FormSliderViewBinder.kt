package com.thejuki.kformmaster.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.github.vivchar.rendererrecyclerviewadapter.ViewState
import com.github.vivchar.rendererrecyclerviewadapter.ViewStateProvider
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.model.FormSliderElement
import com.thejuki.kformmaster.state.FormSliderViewState
import kotlin.math.truncate

/**
 * Form Slider Binder
 *
 * View Binder for [FormSliderElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormSliderViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewBinder() {
    val viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element_slider, FormSliderElement::class.java, { model, finder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? LinearLayout
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val slider = finder.find<AppCompatSeekBar>(R.id.formElementValue)
        val maxLabelView = finder.find<AppCompatTextView>(R.id.formElementMaxLabel)
        val minLabelView = finder.find<AppCompatTextView>(R.id.formElementMinLabel)
        val maxDisplayValueView = finder.find<AppCompatTextView>(R.id.formElementMaxValue)
        val minDisplayValueView = finder.find<AppCompatTextView>(R.id.formElementMinValue)
        val progressValue = finder.find<AppCompatTextView>(R.id.formElementProgress)
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout, slider)


        model.maxLabel.takeUnless { it.isBlank()}?.let {
            maxLabelView.text = it
        } ?: run{ maxLabelView.visibility = View.GONE }

        model.minLabel.takeUnless { it.isBlank()}?.let {
            minLabelView.text = it
        } ?: run{ minLabelView.visibility = View.GONE }

        maxDisplayValueView.text = model.max.toString()
        minDisplayValueView.text = model.min.toString()


        if (model.value == null) {
            model.setValue(model.min)
        }

        slider.progress = getProgressValue(model.value?:model.min,model)
        slider.max = 100

        progressValue.text = model.value.toString()

        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSeekValue(model, slider, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        itemView.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()
        }
    }, object : ViewStateProvider<FormSliderElement<*>, ViewHolder> {
        override fun createViewStateID(model: FormSliderElement<*>): Int {
            return model.id
        }

        override fun createViewState(holder: ViewHolder): ViewState<ViewHolder> {
            return FormSliderViewState(holder)
        }
    })

    private fun updateSeekValue(model: FormSliderElement<*>,
                                slider: AppCompatSeekBar,
                                sliderValue: Int) {

        model.error = null
        model.setValue(getDisplayedValue(sliderValue, model))
        formBuilder.onValueChanged(model)

        slider.progress = getProgressValue(model.value?:model.min, model)
    }

    // Calcul of the value to be displayed
    private fun getDisplayedValue(progressVal: Int, model : FormSliderElement<*>) = when {
        progressVal <= 0 ->
            model.min.toDouble()
        progressVal >= 100 ->
            model.max.toDouble()
        model.steps != null ->
        {
            if (model.steps!!.toDouble() >= 1.toDouble()) {
                val wantedValue = ((progressVal.toDouble() * ((model.max.toDouble() - model.min.toDouble() )/(100))) + model.min.toDouble() )
                val increment = ((model.max.toDouble() - model.min.toDouble()) / ( model.steps?.toDouble() ?:1.toDouble()))
                val stepAmountBeforMax: Double = truncate((model.max.toDouble() - wantedValue) / increment)
                val stepAmount: Double = ((model.max.toDouble() - model.min.toDouble() ) / increment)
                (((stepAmount - stepAmountBeforMax) * increment) + model.min.toDouble() )
            } else {model.min }
        }
        model.incrementBy != null -> {
            val wantedValue = ((progressVal.toDouble() * ((model.max.toDouble() - model.min.toDouble() )/(100))) + model.min.toDouble() )
            val stepAmountBeforMax: Double = truncate((model.max.toDouble() - wantedValue) / ( model.incrementBy?.toDouble() ?:1.toDouble()))
            val stepAmount: Double = ((model.max.toDouble() - model.min.toDouble() ) / ( model.incrementBy?.toDouble() ?:1.toDouble()))
            (((stepAmount - stepAmountBeforMax) * ( model.incrementBy?.toDouble() ?:1.toDouble())) + model.min.toDouble() )
        }
        model.incrementBy == null && model.steps == null ->
            ((progressVal.toDouble() * ((model.max.toDouble() - model.min.toDouble() )/(100))) + model.min.toDouble() ).toDouble()
        else ->
            model.min
    }.let { if(model.value is Int) it.toInt() else it }

    // Calcul of the value to be set on the seekbar
    private fun getProgressValue(valueToDisplay: Number, model : FormSliderElement<*>) = when {
        (valueToDisplay.toDouble()) <= model.min.toDouble() -> 0
        (valueToDisplay.toDouble()) >= model.max.toDouble() -> 100
        else -> (((valueToDisplay.toDouble() - model.min.toDouble()) / (model.max.toDouble() - model.min.toDouble())) * 100).toInt()

    }

}
