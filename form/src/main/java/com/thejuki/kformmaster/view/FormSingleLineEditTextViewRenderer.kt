package com.thejuki.kformmaster.view

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.google.android.material.card.MaterialCardView
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.model.FormSingleLineEditTextElement
import com.thejuki.kformmaster.widget.ClearableEditText

/**
 * Form SingleLine EditText ViewRenderer
 *
 * View Binder for [FormSingleLineEditTextElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormSingleLineEditTextViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewRenderer() {
    val viewRenderer = ViewRenderer(layoutID ?: R.layout.form_element, FormSingleLineEditTextElement::class.java) { model, finder: FormViewFinder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? MaterialCardView
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val editTextValue = finder.find(R.id.formElementValue) as ClearableEditText
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout, editTextValue)

        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
            tip?.visibility = View.VISIBLE
        }

        editTextValue.setText(model.valueAsString)
        editTextValue.hint = model.hint ?: ""

        // Single Line
        editTextValue.maxLines = 1

        // If an InputType is provided, use it instead
        model.inputType?.let { editTextValue.setRawInputType(it) }

        // If imeOptions are provided, use them instead of actionNext
        model.imeOptions?.let { editTextValue.imeOptions = it }

        setEditTextFocusEnabled(model, editTextValue, itemView)
        setOnFocusChangeListener(model, formBuilder)
        addTextChangedListener(model, formBuilder)
        setOnEditorActionListener(model, formBuilder)
        setClearableListener(model)

        editTextValue.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()
        }

    }

    private fun setEditTextFocusEnabled(model: FormSingleLineEditTextElement,
                                        editTextValue: AppCompatEditText,
                                        itemView: View) {
        itemView.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()

            editTextValue.requestFocus()
            val imm = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            editTextValue.setSelection(editTextValue.text?.length ?: 0)
            imm.showSoftInput(editTextValue, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}
