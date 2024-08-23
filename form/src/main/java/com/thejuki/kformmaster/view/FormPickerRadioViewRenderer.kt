package com.thejuki.kformmaster.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.google.android.material.card.MaterialCardView
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.model.FormPickerDropDownElement
import com.thejuki.kformmaster.model.FormPickerRadioElement

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
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? MaterialCardView
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val editValue = finder.find(R.id.formElementValue) as RadioGroup
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView


        editValue.removeAllViews()

        model.optionsToDisplay().forEachIndexed { index, string ->
            var radioButton= RadioButton(editValue.context)
            radioButton.text = string
            radioButton.id = index
            editValue.addView(radioButton)
        }


        for(i in 0 until editValue.childCount){
            if((editValue.getChildAt(i) as? RadioButton)?.text?.equals(model.valueAsString) == true){
                editValue.check(i)
            }
        }


        editValue.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            model.onSelectValue((group.getChildAt(checkedId) as RadioButton).text.toString())
            formBuilder.onValueChanged(model)
        })
      /* editValue.setOnCheckedChangeListener { _: ViewGroup?, radioButton: RadioButton? ->
            radioButton?.let{
                model.onSelectValue(it.text.toString())
                formBuilder.onValueChanged(model)
            }
        }
        if (model.options?.size?:0 < 2) {
            editValue.maxInRow = 2
        } else {
            editValue.maxInRow = 1
        }
        for (i in 1..editValue.childCount) {
            editValue.getRadioButtonAt(i).maxLines = 3
        }
*/
        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
            tip?.visibility = View.VISIBLE
        }





        baseSetup(model, dividerView, textViewTitle, textViewError, itemView,mainViewLayout, editView =  editValue)

        setClearableListener(model)

    }
}
