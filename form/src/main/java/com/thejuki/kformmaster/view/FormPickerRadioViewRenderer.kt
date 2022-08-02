package com.thejuki.kformmaster.view

import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.model.FormPickerDropDownElement
import com.thejuki.kformmaster.model.FormPickerRadioElement
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup

/**
 * Form Picker DropDown ViewBinder
 *
 * View Binder for [FormPickerDropDownElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormPickerRadioViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewRenderer() {
    var viewRenderer = ViewRenderer(layoutID
            ?: R.layout.form_element_radio, FormPickerRadioElement::class.java) { model, finder: FormViewFinder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val editValue = finder.find(R.id.formElementValue) as MultiLineRadioGroup
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView


        editValue.removeAllButtons()

        model.optionsToDisplay().forEachIndexed { index, string ->
            editValue.addButtons(index, string)
        }

        editValue.check(model.valueAsString)

        editValue.setOnCheckedChangeListener { _: ViewGroup?, radioButton: RadioButton? ->
            radioButton?.let{
                model.onSelectValue(it.text.toString())
                formBuilder.onValueChanged(model)
            }
        }

        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
            tip?.visibility = View.VISIBLE
        }

        if (model.options?.size?:0 < 2) {
            editValue.maxInRow = 2
        } else {
            editValue.maxInRow = 1
        }

        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, editView =  editValue)

        setClearableListener(model)

    }
}
