package com.thejuki.kformmaster.view

import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.model.FormTextViewElement

/**
 * Form TextView ViewRenderer
 *
 * View Binder for [FormTextViewElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormTextViewViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewRenderer() {
    val viewRenderer = ViewRenderer(layoutID
            ?: R.layout.form_element, FormTextViewElement::class.java) { model, finder: FormViewFinder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? LinearLayout
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val editTextValue = finder.find(R.id.formElementValue) as com.thejuki.kformmaster.widget.ClearableEditText
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout, editTextValue)

        editTextValue.setText(model.valueAsString)
        editTextValue.hint = model.hint ?: ""
        editTextValue.isEnabled = false
        editTextValue.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorFormMasterElementTextView))
        editTextValue.isFocusable = false
        editTextValue.setRawInputType(InputType.TYPE_NULL)
        editTextValue.setClearIconLocation(null)

        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
            tip?.visibility = View.VISIBLE
        }

        itemView.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()
        }

        editTextValue.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()
        }

    }
}
