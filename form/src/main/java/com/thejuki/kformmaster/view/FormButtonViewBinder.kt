package com.thejuki.kformmaster.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatButton
import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.github.vivchar.rendererrecyclerviewadapter.ViewState
import com.github.vivchar.rendererrecyclerviewadapter.ViewStateProvider
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.model.FormButtonElement
import com.thejuki.kformmaster.state.FormButtonViewState
import com.thejuki.kformmaster.widget.IconButton

/**
 * Form Button ViewBinder
 *
 * View Binder for [FormButtonElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormButtonViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewBinder() {
    val viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element_button, FormButtonElement::class.java, { model, finder, _ ->
        val itemView = finder.getRootView() as View
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val button = finder.find(R.id.formElementValue) as AppCompatButton
        baseSetup(model, dividerView, itemView = itemView, editView = button)

        button.text = model.text

        button.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()
        }

        model.iconLocation = IconButton.Location.valueOf(model.titleIconLocation.toString())
        model.icon = model.titleIcon
        model.iconPadding = model.titleIconPadding

        setIconVisible(button, model.icon, model.iconLocation, model.iconPadding)

    }, object : ViewStateProvider<FormButtonElement, ViewHolder> {
        override fun createViewStateID(model: FormButtonElement): Int {
            return model.id
        }

        override fun createViewState(holder: ViewHolder): ViewState<ViewHolder> {
            return FormButtonViewState(holder)
        }
    })

    private fun setIconVisible(button : AppCompatButton, icon: Drawable?, location: IconButton.Location, iconPadding: Int) {
        val cd = button.compoundDrawables

        // Reset icons
        if (cd[0] == icon) {
            cd[0] = null
        }
        if (cd[2] == icon) {
            cd[2] = null
        }

        button.setCompoundDrawables(if (location == IconButton.Location.LEFT) icon else cd[0], cd[1], if (location == IconButton.Location.RIGHT) icon else cd[2],
                cd[3])
        button.compoundDrawablePadding = iconPadding
    }
}
