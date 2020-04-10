package com.thejuki.kformmaster.view

import android.content.Context
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.github.vivchar.rendererrecyclerviewadapter.ViewState
import com.github.vivchar.rendererrecyclerviewadapter.ViewStateProvider
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.model.FormPickerMultiCheckBoxElement
import com.thejuki.kformmaster.state.FormEditTextViewState

/**
 * Form Picker MultiCheckBox ViewBinder
 *
 * View Binder for [FormPickerMultiCheckBoxElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormPickerMultiCheckBoxViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewBinder() {
    val viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element_selector_multi, FormPickerMultiCheckBoxElement::class.java, { model, finder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? LinearLayout
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        val editValue = finder.find(R.id.formElementValue) as TableLayout

        editValue.isFocusable = false

        setClearableListener(model)
        editValue.removeAllViewsInLayout()
        model.getSelectedItems().forEach {selectedItemText ->
            editValue.addView(
                    TextView(context).also { it.text = selectedItemText }
            )
        }
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout, editValue)
        model.reInitDialog(formBuilder)



    }, object : ViewStateProvider<FormPickerMultiCheckBoxElement<*>, ViewHolder> {
        override fun createViewStateID(model: FormPickerMultiCheckBoxElement<*>): Int {
            return model.id
        }

        override fun createViewState(holder: ViewHolder): ViewState<ViewHolder> {
            return FormEditTextViewState(holder)
        }
    })
}
