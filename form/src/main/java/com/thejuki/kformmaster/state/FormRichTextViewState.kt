package com.thejuki.kformmaster.state

import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.thejuki.kformmaster.R
import jp.wasabeef.richeditor.RichEditor

/**
 * Form RichText ViewState
 *
 * View State for any RichTextElement
 *
 * @author **Julien Pouget**
 * @version 1.0
 */
class FormRichTextViewState(holder: ViewHolder) : BaseFormViewState(holder) {
    private var value: String? = null

    init {
        val richEditor = holder.viewFinder.find(R.id.formElementValue) as RichEditor
        value = richEditor.html
    }

    override fun restore(holder: ViewHolder) {
        super.restore(holder)
        holder.viewFinder.setText(R.id.formElementValue, value)
    }
}