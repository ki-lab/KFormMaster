package com.thejuki.kformmaster.model

/**
 * Form Number EditText Element
 *
 * Form element for AppCompatEditText
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormNumberEditTextElement(tag: Int = -1) : BaseFormElement<Number>(tag) {

    /**
     * By default, the number field can contain numbers and symbols (.,-).
     * Set to true to only allow numbers.
     */
    var numbersOnly: Boolean = false

    override fun setValue(value: Any?): BaseFormElement<Number> {
        var newValue = value
        if (newValue != null && newValue is String) {
            newValue = if (newValue.isBlank()) {
                null
            } else if (newValue.contains(",") || newValue.contains(".")) {
                newValue.replace(",", ".").toDoubleOrNull()
            } else {
                newValue.toIntOrNull()
            }
        }

        return super.setValue(newValue)
    }
}