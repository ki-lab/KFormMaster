package com.thejuki.kformmaster.view

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.github.vivchar.rendererrecyclerviewadapter.ViewState
import com.github.vivchar.rendererrecyclerviewadapter.ViewStateProvider
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.model.BaseFormElement
import com.thejuki.kformmaster.model.FormRichEditTextElement
import com.thejuki.kformmaster.state.FormEditTextViewState
import jp.wasabeef.richeditor.RichEditor

/**
 * Form Rich EditText ViewBinder
 *
 * View Binder for [FormRichEditTextElement]
 *
 * @author **Julien Pouget**
 * @version 1.0
 */
class FormRichEditTextViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewBinder() {
    val viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element_rich_text, FormRichEditTextElement::class.java, { model, finder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? LinearLayout
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val richEditor = finder.find(R.id.formElementValue) as RichEditor
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout, richEditor)

        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
            tip?.visibility = View.VISIBLE
        }

        richEditor.html = model.valueAsString

        richEditor.setPlaceholder(model.hint ?: "")
        richEditor.setInputEnabled(model.enabled)

        setEditTextFocusEnabled(model, richEditor, itemView)
        setOnFocusChangeListener(context, model, formBuilder)
        addTextChangedListener(model, formBuilder)

        richEditor.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()
        }

    }, object : ViewStateProvider<FormRichEditTextElement, ViewHolder> {
        override fun createViewStateID(model: FormRichEditTextElement): Int {
            return model.id
        }

        override fun createViewState(holder: ViewHolder): ViewState<ViewHolder> {
            return FormEditTextViewState(holder)
        }
    })

    private fun setEditTextFocusEnabled(model: FormRichEditTextElement,
                                        richEditor: RichEditor,
                                        itemView: View) {

        itemView.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()

            richEditor.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(richEditor, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}
