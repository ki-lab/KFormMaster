package com.thejuki.kformmasterexample.custom.view

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.view.BaseFormViewRenderer
import com.thejuki.kformmaster.widget.ClearableEditText
import com.thejuki.kformmasterexample.R
<<<<<<<< HEAD:app/src/main/java/com/thejuki/kformmasterexample/custom/view/FormCustomExtViewBinder.kt
import com.thejuki.kformmasterexample.custom.model.FormCustomExtElement
import com.thejuki.kformmasterexample.custom.state.FormCustomViewState
========
import com.thejuki.kformmasterexample.custom.model.FormCustomElement
>>>>>>>> heads/8.2.1.1:app/src/main/java/com/thejuki/kformmasterexample/custom/view/FormCustomViewRenderer.kt

/**
 * Form Custom ViewRenderer
 *
 * View Binder for [FormCustomExtElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
<<<<<<<< HEAD:app/src/main/java/com/thejuki/kformmasterexample/custom/view/FormCustomExtViewBinder.kt
class CustomExtViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewBinder() {
    var viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element_custom, FormCustomExtElement::class.java, { model, finder, _ ->
========
class CustomViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int? = null) : BaseFormViewRenderer() {
    val viewRenderer = ViewRenderer(layoutID
            ?: R.layout.form_element_custom, FormCustomElement::class.java) { model, finder: FormViewFinder, _ ->
>>>>>>>> heads/8.2.1.1:app/src/main/java/com/thejuki/kformmasterexample/custom/view/FormCustomViewRenderer.kt
        val textViewTitle = finder.find(R.id.formElementTitle) as AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? LinearLayout
        val textViewError = finder.find(R.id.formElementError) as AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val editTextValue = finder.find(R.id.formElementValue) as ClearableEditText
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout, editTextValue)

        editTextValue.setText(model.valueAsString)
        editTextValue.hint = model.hint ?: ""

        // Initially use 4 lines
        // unless a different number was provided
        if (model.maxLines == 1) {
            model.maxLines = 4
        }

        // If an InputType is provided, use it instead
        model.inputType?.let { editTextValue.setRawInputType(it) }

        // If imeOptions are provided, use them instead of creating a new line
        model.imeOptions?.let { editTextValue.imeOptions = it }

        setEditTextFocusEnabled(editTextValue, itemView)
        setOnFocusChangeListener(model, formBuilder)
        addTextChangedListener(model, formBuilder)
        setOnEditorActionListener(model, formBuilder)
<<<<<<<< HEAD:app/src/main/java/com/thejuki/kformmasterexample/custom/view/FormCustomExtViewBinder.kt

    }, object : ViewStateProvider<FormCustomExtElement, ViewHolder> {
        override fun createViewStateID(model: FormCustomExtElement): Int {
            return model.id
        }

        override fun createViewState(holder: ViewHolder): ViewState<ViewHolder> {
            return FormCustomViewState(holder)
        }
    })
========
    }
>>>>>>>> heads/8.2.1.1:app/src/main/java/com/thejuki/kformmasterexample/custom/view/FormCustomViewRenderer.kt

    private fun setEditTextFocusEnabled(editTextValue: AppCompatEditText, itemView: View) {
        itemView.setOnClickListener {
            editTextValue.requestFocus()
            val imm = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            editTextValue.setSelection(editTextValue.text?.length ?: 0)
            imm.showSoftInput(editTextValue, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}
