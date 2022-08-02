package com.thejuki.kformmaster.view

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.model.FormRichEditTextElement
import jp.wasabeef.richeditor.RichEditor

/**
 * Form Rich EditText ViewBinder
 *
 * View Binder for [FormRichEditTextElement]
 *
 * @author **Julien Pouget**
 * @version 1.0
 */
class FormRichEditTextViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewRenderer() {
    var viewRenderer = ViewRenderer(layoutID
            ?: R.layout.form_element_rich_text, FormRichEditTextElement::class.java) { model, finder: FormViewFinder, _ ->
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

        setOnClickListener(model, richEditor, itemView)
        setOnFocusChangeListener(model, formBuilder)
        addTextChangedListener(model, formBuilder)

    }

    private fun setOnClickListener(model: FormRichEditTextElement, richEditor: RichEditor, itemView: View) {

        itemView.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()

            richEditor.focusEditor()

            val imm = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(richEditor, InputMethodManager.SHOW_IMPLICIT)
        }

        richEditor.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()
        }
    }
}
