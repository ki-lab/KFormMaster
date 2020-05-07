package com.thejuki.kformmaster.model

import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView

interface FormPlugin {
    var callback : () -> Any?
}

open class FormTreeElement<T>(tag: Int = -1) : BaseFormElement<T>(tag), FormPlugin {
    override fun clear() {
       // this.items = unCheckedValue
    }

    /**
     * Sets the list of items
     */
    var items : List<TreeItem> = arrayListOf()
    override var callback: () -> Any? = {}

    override fun displayNewValue() {
        editView?.let {
          if (it is RecyclerView) {
                it.adapter?.notifyDataSetChanged()
            }
        }
    }

    class TreeItem {
        var id = ""
        var name = ""
        var selected = false
            get() = field || required
        var required = false
        var children = arrayListOf<TreeItem>()
    }
}