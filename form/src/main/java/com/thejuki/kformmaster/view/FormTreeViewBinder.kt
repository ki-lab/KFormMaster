package com.thejuki.kformmaster.view

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.github.vivchar.rendererrecyclerviewadapter.ViewState
import com.github.vivchar.rendererrecyclerviewadapter.ViewStateProvider
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.model.FormCheckBoxElement
import com.thejuki.kformmaster.model.FormTreeElement
import com.thejuki.kformmaster.state.FormCheckBoxViewState
import kotlinx.android.synthetic.main.tree_expandable_list_item.view.*
import kotlinx.android.synthetic.main.tree_expandable_list_topic.view.*
import kotlinx.android.synthetic.main.tree_list_item.view.*

class FormTreeViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewBinder() {

    private lateinit var model:  FormTreeElement<*>
    
    private val displayedItems
        get() = model.items.flatMap{ it.children.filter { child -> child.selected } }

    val viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element_tree, FormTreeElement::class.java) { _model, finder, _ ->
        model = _model
        val textViewTitle = finder.find(R.id.formElementTitle) as? AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? ConstraintLayout
        val textViewError = finder.find(R.id.formElementError) as? AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val recyclerView = finder.find(R.id.formElementRecyclerView) as? RecyclerView
        val tip = finder.find(R.id.formElementTip) as? AppCompatTextView
        val button = finder.find(R.id.formElementAdd) as? AppCompatButton
        val itemView = finder.getRootView() as View

        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, editView = recyclerView)

        if(model.tip.isNotEmpty()) {
            tip?.text = model.tip
        } else {
            tip?.visibility = View.GONE
        }

        // Delay setting to make sure editView is set first
        model.mainLayoutView = mainViewLayout

        button?.setOnClickListener {
            openDialog(recyclerView)
        }

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = ListAdapter()
    }

    private fun openDialog(recyclerView: RecyclerView?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(model.title)

        val expandableListView = ExpandableListView(context)

        val expandableListAdapter = TreeAdapter(model.items)
        builder.setPositiveButton("OK") { dialog: DialogInterface, _ : Int ->
            recyclerView?.adapter?.notifyDataSetChanged()
            model.callback()
            dialog.dismiss()
        }
        builder.setNegativeButton("ANNULER") { dialog: DialogInterface, id: Int -> dialog.dismiss() }
        expandableListView.setAdapter(expandableListAdapter)
        builder.setView(expandableListView)
        val dialog = builder.create()
        dialog.show()
    }

    private fun setCheckBoxFocusEnabled(model: FormCheckBoxElement<*>, itemView: View, checkBox: AppCompatCheckBox) {
        itemView.setOnClickListener {
            // Invoke onClick Unit
            model.onClick?.invoke()

            checkBox.isChecked = !checkBox.isChecked
        }
    }

    inner class TreeAdapter(private val input: List<FormTreeElement.TreeItem>) : BaseExpandableListAdapter() {

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
            val view = convertView
                    ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                            .inflate(R.layout.tree_expandable_list_topic, parent, false)
            view.topic.text = input[groupPosition].name
            return view
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {

            val view = convertView
                    ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                            .inflate(R.layout.tree_expandable_list_item, parent, false)
            view.item.text = input[groupPosition].children[childPosition].name

            view.item.setOnCheckedChangeListener { _, isChecked ->
                input[groupPosition].children[childPosition].selected = isChecked
            }

            view.item.isChecked = input[groupPosition].children[childPosition].selected
            view.item.isEnabled = !input[groupPosition].children[childPosition].required

            return view
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int) = false
        override fun hasStableIds() = false // true
        override fun getGroup(groupPosition: Int) = input.getOrNull(groupPosition)
        override fun getChild(groupPosition: Int, childPosition: Int) = input.getOrNull(groupPosition)?.children?.getOrNull(childPosition)
        override fun getGroupId(groupPosition: Int) = 0L // input.getOrNull(groupPosition)?.id?.toLong()
                ?: 0L

        override fun getChildId(groupPosition: Int, childPosition: Int) = 0L //input.getOrNull(groupPosition)?.children?.getOrNull(childPosition)?.id?.toLong()
                ?: 0L

        override fun getGroupCount() = input.size
        override fun getChildrenCount(groupPosition: Int) = input.getOrNull(groupPosition)?.children?.size
                ?: 0

    }

    inner class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tree_list_item, parent, false)
            return ViewHolder(itemView).apply {
                itemView.removeButton.setOnClickListener {
                    displayedItems[adapterPosition].selected = !displayedItems[adapterPosition].selected
                    model.callback()
                    notifyDataSetChanged()
                }
            }
        }

        override fun getItemCount() = displayedItems.size
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(displayedItems[position])
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(item: FormTreeElement.TreeItem) = itemView.run {
                itemView.listItem.text = item.name
            }
        }
    }
}