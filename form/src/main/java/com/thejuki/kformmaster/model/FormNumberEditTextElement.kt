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

    override fun setValue(rawValue: Any?): BaseFormElement<Number> {
        var value = rawValue
        if (value != null) {
            if (value is String) {
                if (value.contains(",")) {
                    value = value.replace(",", ".")
                }
            }
        }

        return super.setValue(when{
            (value as? String)?.isBlank() == true -> null
            value is String && (value as? String)?.isNotBlank() == true && (value as? String)?.replace(",", ".")?.contains('.')==true -> (value as? String)?.toDoubleOrNull()?:""
            value is String && (value as? String)?.isNotBlank() == true && (value as? String)?.replace(",", ".")?.contains('.')==false -> (value as? String)?.toIntOrNull()?:""
            else -> value
        })
    }
}