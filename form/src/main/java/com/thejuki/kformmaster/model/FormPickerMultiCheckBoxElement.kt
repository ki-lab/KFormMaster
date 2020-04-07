package com.thejuki.kformmaster.model

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.listener.OnFormElementValueChangedListener
import kotlin.properties.Delegates

/**
 * Form Picker MultiCheckBox Element
 *
 * Form element for AppCompatEditText (which on click opens a MultiCheckBox dialog)
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormPickerMultiCheckBoxElement<T>(tag: Int = -1) : FormPickerElement<T>(tag) {

    override val isValid: Boolean
        get() = validityCheck()

    override var validityCheck = { !required || (listValue != null && (listValue as List<T>?)?.isEmpty() == false) }

    override fun clear() {
        super.clear()
        reInitDialog()
    }

    /**
     * Form Element Value Observers
     */
    val listValueObservers = mutableListOf<(value: List<T>?, element: BaseFormElement<T>) -> Unit>()

    /**
     * Form Element Value
     */
    var listValue: List<T>? by Delegates.observable<List<T>?>(null) { _, _, newValue ->
        listValueObservers.forEach { it(newValue, this) }
        editView?.let {
            displayNewValue()
        }
    }

    override var value: T? by Delegates.observable<T?>(null) { _, _, newValue ->
//        valueObservers.forEach { it(newValue, this) }
        val newListValue: MutableList<T> = mutableListOf()
        listValue?.let { newListValue.addAll(it) }
        newValue?.let { newListValue.add(it) }
        listValue = newListValue
    }
    /**
     * Value builder setter
     */
    @Suppress("UNCHECKED_CAST")
    fun setListValue(value: List<T>): BaseFormElement<T> {
        listValue = value
//        value.forEach {newValue ->
//            listValue?.also {list ->
//                if(!list.contains(newValue)) newValue?.let { list.add(it) }
//            } ?: run{listValue = mutableListOf<T>().also {list-> newValue?.let { list.add(it) } }}
//        }
//        editView?.let {
//            displayNewValue()
//        }
        return this
    }


    /**
     * Form Element Options
     */
    var options: List<T>? = null
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
     * Display Value For
     * Used to specify a string value to be displayed
     */
    var displayValueFor: ((T?) -> String?) = {
        it?.toString() ?: ""
    }

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
     * Hold the [OnFormElementValueChangedListener] from [FormBuildHelper]
     */
    private var listener: OnFormElementValueChangedListener? = null

    /**
     * Theme
     */
    var theme: Int = 0

    /**
     * Re-initializes the dialog
     * Should be called after the options list changes
     */
    fun reInitDialog(formBuilder: FormBuildHelper? = null) {
        // reformat the options in format needed
        val options = arrayOfNulls<CharSequence>(this.options?.size ?: 0)
        val optionsSelected = BooleanArray(this.options?.size ?: 0)
        val mSelectedItems = ArrayList<Int>()

        lateinit var alertDialog: AlertDialog

        this.options?.let {
            for (i in it.indices) {
                val obj = it[i]

                options[i] = obj.toString()
                optionsSelected[i] = false

                if ((this.listValue as List<T>?)?.contains(obj) == true) {
                    optionsSelected[i] = true
                    mSelectedItems.add(i)
                }
            }
        }

        if (formBuilder != null) {
            listener = formBuilder.listener
        }

        var selectedItems = ""
        for (i in mSelectedItems.indices) {
            selectedItems += options[mSelectedItems[i]]

            if (i < mSelectedItems.size - 1) {
                selectedItems += ", "
            }
        }

        val editTextView = this.editView as? AppCompatEditText

        if (alertDialogBuilder == null && editTextView?.context != null) {
            alertDialogBuilder = AlertDialog.Builder(editTextView.context, theme)
            if (this.dialogTitle == null) {
                this.dialogTitle = editTextView.context.getString(R.string.form_master_pick_one_or_more)
            }
            if (this.dialogEmptyMessage == null) {
                this.dialogEmptyMessage = editTextView.context.getString(R.string.form_master_empty)
            }
            if (this.confirmTitle == null) {
                this.confirmTitle = editTextView.context.getString(R.string.form_master_confirm_title)
            }
            if (this.confirmMessage == null) {
                this.confirmMessage = editTextView.context.getString(R.string.form_master_confirm_message)
            }
        }

        alertDialogBuilder?.let { builder ->
            if ((this.options as List<T>)?.isEmpty() == true) {
                builder.setTitle(this.dialogTitle)
                        .setMessage(dialogEmptyMessage)
                        .setPositiveButton(null, null)
                        .setNegativeButton(null, null)
            } else {
                val optionsDisplayed = arrayOfNulls<CharSequence>(this.options?.size ?: 0)
                this.options?.let {
                    for (i in it.indices) {
                        optionsDisplayed[i] = this.displayValueFor(it[i])
                    }
                }
                builder.setTitle(this.dialogTitle)
                        .setMessage(null)
                        .setMultiChoiceItems(optionsDisplayed, optionsSelected) { _, which, isChecked ->
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                mSelectedItems.add(which)
                            } else if (mSelectedItems.contains(which)) {
                                // Else, if the item is already in the array, remove it
                                mSelectedItems.remove(which)
                            }
                        }
                        // Set the action buttons
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            this.options?.let { option ->
                                val selectedOptions = mSelectedItems.indices
                                        .map { mSelectedItems[it] }
                                        .map { x -> option[x] }

                                this.error = null
                                this.setListValue(selectedOptions)
                                listener?.onValueChanged(this)
                            }
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> }
            }

            alertDialog = builder.create()
        }

        // display the dialog on click
        val listener = View.OnClickListener {
            // Invoke onClick Unit
            this.onClick?.invoke()

            if (!confirmEdit || listValue == null || (listValue as List<T>?)?.isEmpty() == true) {
                alertDialog.show()
            } else if (confirmEdit && listValue != null) {
                alertDialogBuilder
                        ?.setTitle(confirmTitle)
                        ?.setMessage(confirmMessage)
                        ?.setPositiveButton(android.R.string.ok) { _, _ ->
                            alertDialog.show()
                        }?.setNegativeButton(android.R.string.cancel) { _, _ -> }?.show()
            }
        }

        itemView?.setOnClickListener(listener)
        editTextView?.setOnClickListener(listener)
    }

    fun getSelectedItemsText(): String {
        val options = arrayOfNulls<CharSequence>(this.options?.size ?: 0)
        val mSelectedItems = ArrayList<Int>()

        this.options?.let {
            for (i in it.indices) {
                options[i] = this.displayValueFor(it[i])

                if ((this.listValue as List<T>?)?.contains(it[i]) == true) {
                    mSelectedItems.add(i)
                }
            }
        }

        var selectedItems = ""
        for (i in mSelectedItems.indices) {
            selectedItems += options[mSelectedItems[i]]

            if (i < mSelectedItems.size - 1) {
                selectedItems += ", "
            }
        }

        return selectedItems
    }

    override fun displayNewValue() {
        editView?.let {
            if (it is TextView) {
                it.text = getSelectedItemsText()
            }
        }
    }
}
