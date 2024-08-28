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
        var isFloatingPointNumber = false
        if (newValue != null) {
            if (newValue is String) {
                if (newValue.isBlank()) {
                    newValue = null
                } else if (newValue.contains(",")) {
                    isFloatingPointNumber = true
                    newValue = newValue.replace(",", ".")
                }
            }
        }

        return super.setValue(if (isFloatingPointNumber) (newValue as? String)?.toDoubleOrNull() else  (newValue as? String)?.toIntOrNull())
    }
}