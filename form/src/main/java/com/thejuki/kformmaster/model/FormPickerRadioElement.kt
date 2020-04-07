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
     * Enable to display the radio buttons
     */
    var displayRadioButtons: Boolean = false

    /**
     * Alert Dialog Builder
     * Used to call reInitDialog without needing context again.
     */
    private var alertDialogBuilder: AlertDialog.Builder? = null

    /**
     * Alert Dialog Title
     * (optional - uses R.string.form_master_pick_one)
     */
    var dialogTitle: String? = null

    /**
     * Alert Dialog Empty Message
     * (optional - uses R.string.form_master_empty)
     */
    var dialogEmptyMessage: String? = null

    /**
     * ArrayAdapter for Alert Dialog
     * (optional - uses setItems(options))
     */
    var arrayAdapter: ArrayAdapter<*>? = null

    /**
     * Hold the [OnFormElementValueChangedListener] from [FormBuildHelper]
     */
    private var listener: OnFormElementValueChangedListener? = null

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

    fun optionsToDisplay(): List<String>{
        val valuesToDisplay: MutableList<String> = mutableListOf()
        options?.forEach { valuesToDisplay.add(displayValueFor(it)?:"")}
        return valuesToDisplay
    }

    fun getOptionsFromDisplay(valueDisplayed: String) =  options?.find { displayValueFor(it) == valueDisplayed}
}

