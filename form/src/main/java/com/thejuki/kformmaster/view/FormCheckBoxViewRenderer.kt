package com.thejuki.kformmaster.view

import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.google.android.material.card.MaterialCardView
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.model.FormCheckBoxElement

/**
 * Form CheckBox Binder
 *
 * View Binder for [FormCheckBoxElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormCheckBoxViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewRenderer() {
    val viewRenderer = ViewRenderer(layoutID
            ?: R.layout.form_element_checkbox, FormCheckBoxElement::class.java) { model, finder: FormViewFinder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? MaterialCardView
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val checkBox = finder.find(R.id.formElementValue) as AppCompatCheckBox
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout, checkBox)

        checkBox.isChecked = model.isChecked()

        setCheckBoxFocusEnabled(model, itemView, checkBox)

        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
            tip?.visibility = View.VISIBLE
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            model.error = null
            if (isChecked) {
                model.setValue(model.checkedValue)
            } else {
                model.setValue(model.unCheckedValue)
            }
            formBuilder.onValueChanged(model)
        }

        checkBox.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()
        }
    }

    private fun setCheckBoxFocusEnabled(model: FormCheckBoxElement<*>, itemView: View, checkBox: AppCompatCheckBox) {
        itemView.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()

            checkBox.isChecked = !checkBox.isChecked
        }
    }
}
