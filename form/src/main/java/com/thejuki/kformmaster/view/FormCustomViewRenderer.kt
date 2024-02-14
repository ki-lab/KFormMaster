package com.thejuki.kformmaster.view

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.model.FormCustomElement

class FormCustomViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewRenderer() {
    var viewRenderer = ViewRenderer(layoutID
        ?: R.layout.form_element_custom, FormCustomElement::class.java) { model, finder: FormViewFinder, _ ->
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

        model.buttonDrawable?.let{button?.findViewById<AppCompatImageView>(R.id.formElementButton_image)?.setImageDrawable(it)}

        if(!model.displayButton) button?.visibility = View.GONE

        // Delay setting to make sure editView is set first
        model.mainLayoutView = mainViewLayout

        viewContainer?.removeAllViews()
        model.callback?.onDrawCustomView()?.let {
            viewContainer?.addView(it)
        }

        mainViewLayout?.setOnClickListener {
            model.takeIf { it.enabled }?.callback?.onLayoutClicked()
        }

        button?.setOnClickListener {
            model.takeIf { it.enabled }?.callback?.onButtonClicked()
        }
    }
}
