package com.thejuki.kformmaster.model

import android.view.View
import android.view.ViewGroup

interface FormCustomView{
    fun onDrawCustomView(): View?
    fun onLayoutClicked()
    fun onButtonClicked()
}

open class FormCustomElement(tag: Int = -1) : BaseFormElement<String>(tag) {
    override fun clear() {
        // this.items = unCheckedValue
    }

    var callback: FormCustomView? = null
    var customView: View? = null

    override fun displayNewValue() {
        (editView as? ViewGroup)?.let {
            it.removeAllViews()
            callback?.onDrawCustomView()?.let { customView ->
                it.addView(customView)
            }
        }
    }
}