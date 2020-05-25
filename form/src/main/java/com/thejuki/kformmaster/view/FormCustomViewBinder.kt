package com.thejuki.kformmaster.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.model.FormCustomElement

class FormCustomViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewBinder() {

    private lateinit var model: FormCustomElement

    val viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element_custom, FormCustomElement::class.java) { _model, finder, _ ->
        model = _model
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? ConstraintLayout
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val button = finder.find(R.id.formElementButton) as? View
        val viewContainer = finder.find(R.id.formElementCustomViewContainer) as? ViewGroup
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView
        val itemView = finder.getRootView() as View

        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, editView = viewContainer)

        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
            tip?.visibility = View.VISIBLE
        }

        model.buttonDrawable?.let{button?.findViewById<AppCompatImageView>(R.id.formElementButton_image)}

        if(!model.displayButton) button?.visibility = View.GONE

        // Delay setting to make sure editView is set first
        model.mainLayoutView = mainViewLayout

        viewContainer?.removeAllViews()
        model.callback?.onDrawCustomView()?.let {
            viewContainer?.addView(it)
        }

        mainViewLayout?.setOnClickListener {
            model.callback?.onLayoutClicked()
        }

        button?.setOnClickListener {
            model.callback?.onButtonClicked()
        }
    }
}