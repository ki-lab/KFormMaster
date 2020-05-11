package com.thejuki.kformmaster.model

import android.graphics.drawable.Drawable
import android.widget.TextView
import com.thejuki.kformmaster.widget.IconButton

/**
 * Form Button Element
 *
 * Form element for Button
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormButtonElement(tag: Int = -1) : BaseFormElement<String>(tag) {

    /**
     * No validation needed
     */
    override val isValid: Boolean
        get() = validityCheck()

    override var validityCheck = { true }

    var iconLocation: IconButton.Location = IconButton.Location.LEFT

    var icon: Drawable? = null

    var text: String? = ""

    var iconPadding: Int = 20

    /**
     * Nothing to clear
     */
    override fun clear() {}

    override fun displayNewValue() {
        editView?.let {
            if (it is TextView) {
                it.text = value
            }
        }
    }
}
