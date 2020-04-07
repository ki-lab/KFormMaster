package com.thejuki.kformmaster.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.github.vivchar.rendererrecyclerviewadapter.ViewState
import com.github.vivchar.rendererrecyclerviewadapter.ViewStateProvider
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.model.FormPickerDropDownElement
import com.thejuki.kformmaster.model.FormPickerRadioElement
import com.thejuki.kformmaster.state.FormEditTextViewState
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup

/**
 * Form Picker DropDown ViewBinder
 *
 * View Binder for [FormPickerDropDownElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormPickerRadioViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewBinder() {
    val viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element_radio, FormPickerRadioElement::class.java, { model, finder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val editValue = finder.find(R.id.formElementValue) as MultiLineRadioGroup
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, editView =  editValue)

        model.optionsToDisplay().forEach {
            it.let{editValue.addButtons(it)}
        }

        editValue.setOnCheckedChangeListener { _: ViewGroup?, radioButton: RadioButton? ->
            radioButton?.let{ model.setValue(model.getOptionsFromDisplay(it.text.toString()))}
        }

        if (model.options?.size?:0 < 2) {
            editValue.maxInRow = 2
        } else {
            editValue.maxInRow = 1
        }

        setClearableListener(model)

    }, object : ViewStateProvider<FormPickerRadioElement<*>, ViewHolder> {
        override fun createViewStateID(model: FormPickerRadioElement<*>): Int {
            return model.id
        }

        override fun createViewState(holder: ViewHolder): ViewState<ViewHolder> {
            return FormEditTextViewState(holder)
        }
    })


}
