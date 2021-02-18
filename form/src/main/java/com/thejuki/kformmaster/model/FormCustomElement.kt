
package com.thejuki.kformmaster.model

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup

interface FormCustomView{
    fun onDrawCustomView(): View?
    fun onLayoutClicked()
    fun onButtonClicked()
    fun onElementUpdated(e: FormCustomElement)
}

open class FormCustomElement(tag: Int = -1) : BaseFormElement<String>(tag) {
    override fun clear() {
        // this.items = unCheckedValue
    }

    var buttonDrawable: Drawable? = null

    var displayButton: Boolean = true

    var callback: FormCustomView? = null

    override fun onEnabled(enable: Boolean) {
        super.onEnabled(enable)
        callback?.onElementUpdated(this)
    }
    override fun displayNewValue() {
        (editView as? ViewGroup)?.let {
            it.removeAllViews()
            callback?.onDrawCustomView()?.let { customView ->
                it.addView(customView)
            }
        }
    }
}
