package com.thejuki.kformmaster.model

import android.graphics.Typeface

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

    var editTextSize: Float? = null
    var titleTextSize: Float? = null

    var editTextTypeface: Typeface? = null
    var titleTextTypeface: Typeface? = null


}