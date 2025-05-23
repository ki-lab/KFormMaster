package com.thejuki.kformmaster.view

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
import com.thejuki.kformmaster.model.FormNumberEditTextElement

/**
 * Form Number EditText ViewRenderer
 *
 * View Binder for [FormNumberEditTextElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormNumberEditTextViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewRenderer() {
    val viewRenderer = ViewRenderer(layoutID
            ?: R.layout.form_element_number, FormNumberEditTextElement::class.java) { model, finder: FormViewFinder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? MaterialCardView
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val editTextValue = finder.find(R.id.formElementValue) as com.thejuki.kformmaster.widget.ClearableEditText
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout, editTextValue)

        editTextValue.setText(model.valueAsString)
        editTextValue.hint = model.hint ?: ""

        model.editTextSize?.let {
            editTextValue.textSize = it
        }
        model.titleTextSize?.let {
            textViewTitle?.textSize = it
        }
        model.editTextTypeface?.let {
            editTextValue.typeface = it
        }
        model.titleTextTypeface?.let {
            textViewTitle?.typeface = it
        }
        // Number
        if (model.numbersOnly) {
            editTextValue.inputType = InputType.TYPE_CLASS_NUMBER
        } else {
            editTextValue.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            editTextValue.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editTextValue.removeTextChangedListener(this)
                    val newText = editTextValue.text.toString().replace(",", ".")
                    editTextValue.setText(newText)
                    val newSelectionIndex = (start + count).coerceAtMost(newText.length)
                    editTextValue.setSelection(newSelectionIndex)
                    editTextValue.addTextChangedListener(this)
                }
            })
        }

        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
            tip?.visibility = View.VISIBLE
        }
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

    private fun setEditTextFocusEnabled(model: FormNumberEditTextElement,
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
