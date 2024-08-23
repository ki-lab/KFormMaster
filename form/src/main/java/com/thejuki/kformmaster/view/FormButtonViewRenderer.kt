package com.thejuki.kformmaster.view

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatButton
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.model.FormButtonElement
import com.thejuki.kformmaster.widget.IconButton

/**
 * Form Button ViewRenderer
 *
 * View Binder for [FormButtonElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormButtonViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewRenderer() {
    val viewRenderer = ViewRenderer(layoutID
            ?: R.layout.form_element_button, FormButtonElement::class.java) { model, finder: FormViewFinder, _ ->
        val itemView = finder.getRootView() as View
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? LinearLayout
        val button = finder.find(R.id.formElementValue) as IconButton
        baseSetup(model, dividerView, itemView = itemView, mainViewLayout = mainViewLayout, editView = button)

        button.text = model.valueAsString

        button.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()

            model.setValue(model.value)
            formBuilder.onValueChanged(model)
        }

        model.leftIcon = model.titleIcon
        model.rightIcon = model.rightTitleIcon
        model.iconPadding = model.titleIconPadding

        fun setIconVisible(button : AppCompatButton, leftIcon: Drawable?, rightIcon: Drawable?, iconPadding: Int) {
            val cd = button.compoundDrawables

            if (leftIcon != null) {
                cd[0] = leftIcon
            }
            if (rightIcon != null) {
                cd[2] = rightIcon
            }

            button.setCompoundDrawables(cd[0], cd[1], cd[2], cd[3])
            button.compoundDrawablePadding = iconPadding
        }

        setIconVisible(button, model.leftIcon, model.rightIcon, model.iconPadding)

    }
}
