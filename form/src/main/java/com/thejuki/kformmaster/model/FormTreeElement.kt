package com.thejuki.kformmaster.model

import androidx.appcompat.widget.AppCompatCheckBox

open class FormTreeElement<T>(tag: Int = -1) : BaseFormElement<T>(tag) {
    override fun clear() {
       // this.items = unCheckedValue
    }

    /**
     * Sets the list of items
     */
    var items : List<TreeItem> = arrayListOf()

    override fun displayNewValue() {
        editView?.let {
          /*if (it is AppCompatCheckBox) {
                it.isChecked = isChecked()
            }*/
        }
    }

    class TreeItem {
        var id = 0
        var name = ""
        var selected = false
            get() = field || required
        var required = false
        var children = arrayListOf<TreeItem>()
    }
}