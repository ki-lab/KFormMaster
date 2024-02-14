package com.thejuki.kformmaster.view

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.vivchar.rendererrecyclerviewadapter.ViewRenderer
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.helper.FormViewFinder
import com.thejuki.kformmaster.model.FormCheckBoxElement
import com.thejuki.kformmaster.model.FormTreeElement

@Deprecated("Should be reviewed because the model is stored in the renderer making it unusable for multiple instances of [FormTreeElement] in the same Form.")
class FormTreeViewRenderer(private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewRenderer() {

    private lateinit var model:  FormTreeElement<*>

    private val displayedItems
        get() = model.items.flatMap{ it.children.filter { child -> child.selected } }

    var viewRenderer = ViewRenderer(layoutID
            ?: R.layout.form_element_tree, FormTreeElement::class.java) { _model, finder: FormViewFinder, _ ->
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
            tip?.visibility = View.VISIBLE
        }

        // Delay setting to make sure editView is set first
        model.mainLayoutView = mainViewLayout

        button?.setOnClickListener {
            openDialog(recyclerView, itemView.context)
        }

        recyclerView?.layoutManager = LinearLayoutManager(itemView.context)
        recyclerView?.adapter = ListAdapter()
    }

    private fun openDialog(recyclerView: RecyclerView?, context: Context) {
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
                    ?: (parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                            .inflate(R.layout.tree_expandable_list_topic, parent, false)
            view.findViewById<TextView>(R.id.topic)?.text = input[groupPosition].name
            return view
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
            val view = convertView
                    ?: (parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                            .inflate(R.layout.tree_expandable_list_item, parent, false)
            val item = view.findViewById<CheckBox>(R.id.item)

            item.text = input[groupPosition].children[childPosition].name

            item.setOnCheckedChangeListener { _, isChecked ->
                input[groupPosition].children[childPosition].selected = isChecked
            }

            item.isChecked = input[groupPosition].children[childPosition].selected
            item.isEnabled = !input[groupPosition].children[childPosition].required

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
                itemView.findViewById<Button>(R.id.removeButton).setOnClickListener {
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
                itemView.findViewById<TextView>(R.id.listItem).text = item.name
            }
        }
    }
}