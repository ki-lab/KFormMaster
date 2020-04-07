package com.thejuki.kformmaster.model

import android.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.TextView
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.listener.OnFormElementValueChangedListener


/**
 * Form Picker Dropdown Element
 *
 * Form element for AppCompatEditText (which on click opens a Single Choice dialog)
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormPickerRadioElement<T>(tag: Int = -1) : FormPickerElement<T>(tag) {

    /**
     * Form Element Options
     */
    var options: List<T>? = null
        set(value) {
            field = value
        }

    override fun clear() {
        super.clear()
    }

    /**
     * Theme
     */
    var theme: Int = 0

    /**
     * Display Value For
     * Used to specify a string value to be displayed
     */
    var displayValueFor: ((T?) -> String?) = {
        it?.toString() ?: ""
    }

    override val valueAsString: String
        get() = this.displayValueFor(this.value) ?: ""


    override fun displayNewValue() {
        editView?.let {
            if (it is TextView) {
                it.text = valueAsString
            }
        }
    }

    fun onSelectValue(textValue: String){
        setValue(getOptionsFromDisplay(textValue))
    }

    fun optionsToDisplay(): List<String>{
        val valuesToDisplay: MutableList<String> = mutableListOf()
        options?.forEach { valuesToDisplay.add(displayValueFor(it)?:"")}
        return valuesToDisplay
    }

    private fun getOptionsFromDisplay(valueDisplayed: String) =  options?.find { displayValueFor(it) == valueDisplayed}
}

