package com.thejuki.kformmaster.model

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.extensions.dpToPx
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.listener.OnFormElementValueChangedListener


/**
 * Form Picker MultiCheckBox Element
 *
 * Form element for AppCompatEditText (which on click opens a MultiCheckBox dialog)
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormPickerMultiCheckBoxElement<LI : Any?, T : List<LI>>(tag: Int = -1) : FormPickerElement<T>(tag) {

    override val isValid: Boolean
        get() = validityCheck()

    override var validityCheck = { !required || (value != null && value?.isEmpty() == false) }

    override fun clear() {
        super.clear()
        reInitDialog()
    }

    /**
     * Form Element Options
     */
    var options: T? = null
        set(value) {
            field = value
            reInitDialog()
        }

    /**
     * Alert Dialog Builder
     * Used to call reInitDialog without needing context again.
     */
    private var alertDialogBuilder: AlertDialog.Builder? = null

    /**
     * Alert Dialog Title
     * (optional - uses R.string.form_master_pick_one_or_more)
     */
    var dialogTitle: String? = null

    /**
     * Alert Dialog Empty Message
     * (optional - uses R.string.form_master_empty)
     */
    var dialogEmptyMessage: String? = null

    /**
     * Alert Dialog Divider
     * If set to true, this will add a divider between each items of the alert dialog list
     */
    var displayDialogDivider: Boolean = true

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
    var displayValueFor: ((LI?) -> String?) = {
        it?.toString() ?: ""
    }

    var isOptionSelected: ((LI) -> Boolean) = {
        value?.contains(it) ?: false
    }

    /**
     * Re-initializes the dialog
     * Should be called after the options list changes
     */
    fun reInitDialog(formBuilder: FormBuildHelper? = null) { // reformat the options in format needed
        val items = arrayOfNulls<CharSequence>(this.options?.size ?: 0)
        val checkedItems = BooleanArray(this.options?.size ?: 0)
        val selectedItems = ArrayList<Int>()

        lateinit var alertDialog: AlertDialog

        this.options?.let { options ->
            options.forEachIndexed { index, option ->
                items[index] = this.displayValueFor(option)
                checkedItems[index] = false

                if (isOptionSelected(option)) {
                    checkedItems[index] = true
                    selectedItems.add(index)
                }
            }

            selectedItems.takeIf { it.isNotEmpty() }?.let { items ->
                setValue(items.map { options[it] })
            }
        }

        if (formBuilder != null) {
            listener = formBuilder.listener
        }

        val editTextView = this.editView as? AppCompatEditText
        if (alertDialogBuilder == null && editTextView?.context != null) {
            alertDialogBuilder = AlertDialog.Builder(editTextView.context, theme)
            if (dialogTitle == null) {
                dialogTitle = editTextView.context.getString(R.string.form_master_pick_one_or_more)
            }
            if (dialogEmptyMessage == null) {
                dialogEmptyMessage = editTextView.context.getString(R.string.form_master_empty)
            }
            if (confirmTitle == null) {
                confirmTitle = editTextView.context.getString(R.string.form_master_confirm_title)
            }
            if (confirmMessage == null) {
                confirmMessage = editTextView.context.getString(R.string.form_master_confirm_message)
            }
        }

        alertDialogBuilder?.let { builder ->
            if (options?.isEmpty() == true) {
                builder
                    .setTitle(dialogTitle)
                    .setMessage(dialogEmptyMessage)
                    .setPositiveButton(null, null)
                    .setNegativeButton(null, null)
            } else {
                builder
                    .setTitle(dialogTitle)
                    .setMessage(null)
                    .setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
                        if (isChecked) { // If the user checked the item, add it to the selected items
                            selectedItems.add(which)
                        } else if (selectedItems.contains(which)) { // Else, if the item is already in the array, remove it
                            selectedItems.remove(which)
                        }
                    } // Set the action buttons
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        options?.let { options ->
                            error = null
                            setValue(selectedItems.map { options[it] })
                            listener?.onValueChanged(this)
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
            }

            alertDialog = builder.create()

            if (displayDialogDivider) {
                alertDialog.listView.divider = ContextCompat.getDrawable(builder.context, R.drawable.list_item_divider)
                alertDialog.listView.dividerHeight = 1.dpToPx()
            }
        }

        // display the dialog on click
        val listener = View.OnClickListener { // Invoke onClick Unit
            onClick?.invoke()

            if (!confirmEdit || value == null || value?.isEmpty() == true) {
                alertDialog.show()
            } else if (confirmEdit && value != null) {
                alertDialogBuilder
                    ?.setTitle(confirmTitle)
                    ?.setMessage(confirmMessage)
                    ?.setPositiveButton(android.R.string.ok) { _, _ ->
                        alertDialog.show()
                    }
                    ?.setNegativeButton(android.R.string.cancel) { _, _ -> }?.show()
            }
        }

        itemView?.setOnClickListener(listener)
        editTextView?.setOnClickListener(listener)
    }

    private fun getSelectedItemsText(): String {
        return value?.joinToString(", ") { it.toString() } ?: ""
    }

    var valueAsStringOverride: ((T?) -> String?)? = null

    override val valueAsString
        get() = valueAsStringOverride?.invoke(value) ?: getSelectedItemsText()

    override fun displayNewValue() {
        editView?.let {
            if (it is TextView) {
                it.text = valueAsString
            }
        }
    }
}