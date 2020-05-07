package com.thejuki.kformmaster.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.github.vivchar.rendererrecyclerviewadapter.ViewState
import com.github.vivchar.rendererrecyclerviewadapter.ViewStateProvider
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.model.FormCheckBoxElement
import com.thejuki.kformmaster.state.FormCheckBoxViewState

/**
 * Form CheckBox Binder
 *
 * View Binder for [FormCheckBoxElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormCheckBoxViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewBinder() {
    val viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element_checkbox, FormCheckBoxElement::class.java, { model, finder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? LinearLayout
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val checkBox = finder.find(R.id.formElementValue) as AppCompatCheckBox
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, editView = checkBox)

        checkBox.isChecked = model.isChecked()

        // Delay setting to make sure editView is set first
        model.mainLayoutView = mainViewLayout

        setCheckBoxFocusEnabled(model, itemView, checkBox)

        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
            tip?.visibility = View.VISIBLE
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            model.error = null
            if (isChecked) {
                model.setValue(model.checkedValue)
            } else {
                model.setValue(model.unCheckedValue)
            }
            formBuilder.onValueChanged(model)
        }

        checkBox.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()
        }

    }, object : ViewStateProvider<FormCheckBoxElement<*>, ViewHolder> {
        override fun createViewStateID(model: FormCheckBoxElement<*>): Int {
            return model.id
        }

        override fun createViewState(holder: ViewHolder): ViewState<ViewHolder> {
            return FormCheckBoxViewState(holder)
        }
    })

    private fun setCheckBoxFocusEnabled(model: FormCheckBoxElement<*>, itemView: View, checkBox: AppCompatCheckBox) {
        itemView.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()

            checkBox.isChecked = !checkBox.isChecked
        }
    }
}
