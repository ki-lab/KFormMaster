package com.thejuki.kformmasterexample

import android.app.Activity
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.redmadrobot.inputmask.MaskedTextChangedListener.ValueListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import com.thejuki.kformmaster.helper.*
import com.thejuki.kformmaster.model.*
import com.thejuki.kformmaster.widget.FormElementMargins
import com.thejuki.kformmaster.widget.FormElementPadding
import com.thejuki.kformmaster.widget.IconTextView
import com.thejuki.kformmasterexample.FullscreenFormActivity.Tag.*
import com.thejuki.kformmasterexample.adapter.ContactAutoCompleteAdapter
import com.thejuki.kformmasterexample.adapter.EmailAutoCompleteAdapter
import com.thejuki.kformmasterexample.databinding.ActivityFullscreenFormBinding
import com.thejuki.kformmasterexample.databinding.BottomsheetImageBinding
import com.thejuki.kformmasterexample.item.ContactItem
import com.thejuki.kformmasterexample.item.ListItem
import com.thejuki.kformmasterexample.item.SegmentedListItem
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date


/**
 * Fullscreen Form Activity
 *
 * The Form takes up the whole activity screen
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FullscreenFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenFormBinding
    private lateinit var bottomSheetDialogBinding: BottomsheetImageBinding
    private lateinit var formBuilder: FormBuildHelper
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val startImagePickerForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val imageViewElement = formBuilder.getFormElement<FormImageElement>(
            Tag.ImageViewElement.ordinal)
        imageViewElement.handleActivityResult(result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenFormBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupToolBar()

        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialogBinding = BottomsheetImageBinding.inflate(layoutInflater)
        val sheetView = bottomSheetDialogBinding.root
        bottomSheetDialog.setContentView(sheetView)

        setupForm()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_validate -> {

                    // IMPORTANT: Make sure to clear focus before validating/submitting the form.
                    // This is because if an edit text was focused and edited it will need
                    // to lose focus in order to update the form element value.
                    currentFocus?.clearFocus()

                    // Hide the soft keyboard
                    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    var view = currentFocus
                    if (view == null) {
                        view = View(this)
                    }
                    imm.hideSoftInputFromWindow(view.windowToken, 0)

                    // Validate the form values
                    validate()

                    true
                }
                R.id.action_show_hide -> {
                    showHideAll()
                    true
                }
                R.id.action_clear -> {
                    clear()
                    true
                }
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun clear() {
        formBuilder.clearAll()
    }

    private fun showHideAll() {
        formBuilder.elements.forEach {
            if (it is FormHeader) {
                it.setAllCollapsed(!it.allCollapsed, formBuilder)
            }
        }
    }

    private fun validate() {
        val alert = AlertDialog.Builder(this@FullscreenFormActivity).create()

        if (formBuilder.isValidForm) {
            alert.setTitle(this@FullscreenFormActivity.getString(R.string.FormValid))
        } else {
            alert.setTitle(this@FullscreenFormActivity.getString(R.string.FormInvalid))

            formBuilder.elements.forEach {
                if (!it.isValid) {
                    if (it is FormEmailEditTextElement) {
                        it.error = "Invalid email!"
                    } else {
                        it.error = "This field is required!"
                    }

                }
            }
        }

        alert.setButton(AlertDialog.BUTTON_POSITIVE, this@FullscreenFormActivity.getString(android.R.string.ok), { _, _ -> })
        alert.show()
    }

    private fun setupToolBar() {

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.title = getString(R.string.full_screen_form)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }
    }

    private val fruits = listOf(
            Pair<Int, String>(1, "Banana"),
            Pair<Int, String>(2, "Orange"),
            Pair<Int, String>(3, "Mango"),
            Pair<Int, String>(4, "Guava"),
            Pair<Int, String>(5, "Apple")
    )

    private val fruitsSegmented = listOf(SegmentedListItem(id = 1, name = "Banana", drawableDirection = FormSegmentedElement.DrawableDirection.Top),
            SegmentedListItem(id = 2, name = "Orange", drawableDirection = FormSegmentedElement.DrawableDirection.Left),
            SegmentedListItem(id = 3, name = "Avocado", drawableDirection = FormSegmentedElement.DrawableDirection.Bottom),
            SegmentedListItem(id = 4, name = "Apple", drawableDirection = FormSegmentedElement.DrawableDirection.Right)
    )

    private enum class Tag {
        Email,
        Phone,
        Location,
        Address,
        ZipCode,
        Date,
        Time,
        DateTime,
        Password,
        Radio,
        SingleItem,
        MultiItems,
        AutoCompleteElement,
        AutoCompleteTokenElement,
        ButtonElement,
        TextViewElement,
        LabelElement,
        SwitchElement,
        SliderElement,
        ProgressElement,
        CheckBoxElement,
        SegmentedElement,
        ImageViewElement,
        CustomElement;
    }

    private fun setupForm() {
        formBuilder = form(binding.recyclerView,
                formLayouts = FormLayouts(
                        // Uncomment to replace all text elements with the form_element_custom layout
                        //text = R.layout.form_element_custom
                )) {
            imageView(ImageViewElement.ordinal) {
                activityResultLauncher = startImagePickerForResult
                applyCircleCrop = true
                displayDivider = false
                required = false
                enabled = true
                showChangeImageLabel = true
                changeImageLabel = "Change me"
                displayImageHeight = 200
                displayImageWidth = 200
                theme = R.style.CustomDialogPicker // This is to theme the default dialog when onClickListener is not used.
                //defaultImage = R.drawable.default_image
                //value = "https://via.placeholder.com/200.png" //(String) This needs to be an image URL, data URL, or an image FILE (absolutePath)
                imagePickerOptions = {
                    // This lets you customize the ImagePicker library, specifying Crop, Dimensions and MaxSize options
                    it.cropX = 3f
                    it.cropY = 4f
                    it.maxWidth = 150
                    it.maxHeight = 200
                    it.maxSize = 500
                }
                onSelectImage = { uri, error ->
                    // If uri is null, that means an error occurred trying to select the image
                    if (uri != null) {
                        Toast.makeText(this@FullscreenFormActivity, uri.path, LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@FullscreenFormActivity, error, LENGTH_LONG).show()
                    }
                }

                // Optional: Handle onClickListener yourself. Here I am using a BottomSheet instead of the
                // the default AlertDialog
                bottomSheetDialogBinding.imageBottomSheetCamera.setOnClickListener {
                    bottomSheetDialog.dismiss()
                    this.openImagePicker(ImageProvider.CAMERA)
                }
                bottomSheetDialogBinding.imageBottomSheetGallery.setOnClickListener {
                    bottomSheetDialog.dismiss()
                    this.openImagePicker(ImageProvider.GALLERY)
                }
                bottomSheetDialogBinding.imageBottomSheetRemove.setOnClickListener {
                    bottomSheetDialog.dismiss()
                    this.clearImage()
                }
                bottomSheetDialogBinding.imageBottomSheetClose.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                onClick = {
                    bottomSheetDialog.show()
                }
            }
            header {
                title = getString(R.string.PersonalInfo)
                collapsible = true
                backgroundColor = Color.parseColor("#DDDDDD")
                titleTextColor = Color.BLACK
                editViewGravity = Gravity.CENTER
                allCollapsed = false
            }
            email(Email.ordinal) {
                title = getString(R.string.email)
                editViewGravity = Gravity.CENTER
                displayDivider = false
                rightTitleIcon = ContextCompat.getDrawable(this@FullscreenFormActivity, R.drawable.ic_email_blue_24dp)
                titleIconPadding = 5
                titlePadding = FormElementPadding(0, 0, 70, 0)
                hint = getString(R.string.email_hint)
                value = "mail@mail.com"
                maxLines = 3
                maxLength = 100
                backgroundColor = Color.WHITE
                titleTextColor = Color.BLACK
                titleFocusedTextColor = Color.parseColor("#FF4081")
                valueTextColor = Color.BLACK
                errorTextColor = ResourcesCompat.getColor(resources, R.color.colorFormMasterElementErrorTitle, null)
                hintTextColor = Color.BLUE
                enabled = true
                required = true
                clearable = true
                clearOnFocus = false
                validityCheck = {
                    if (value != null) android.util.Patterns.EMAIL_ADDRESS.matcher(value ?: "").matches() else false
                }
                onTitleIconClick = {
                    Toast.makeText(this@FullscreenFormActivity, "Icon clicked", LENGTH_SHORT).show()
                }
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }

            }
            password(Password.ordinal) {
                title = getString(R.string.password)
                value = "Password123"
                displayDivider = false
                required = true
                maxLength = 100
                editViewGravity = Gravity.START
                enabled = true
                clearable = true
                clearOnFocus = false
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
            }
            phone(Phone.ordinal) {
                title = getString(R.string.Phone)
                value = "123-456-7890"
                editViewGravity = Gravity.START
                displayDivider = false
                maxLength = 100
                maxLines = 3
                required = true
                enabled = true
                clearable = true
                clearOnFocus = false
                valueObservers.add { newValue, element ->
                    //Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
                inputMaskOptions = InputMaskOptions(primaryFormat = "+1 ([000]) [000]-[0000]",
                        affinityCalculationStrategy = AffinityCalculationStrategy.PREFIX,
                        valueListener = object : ValueListener {
                            override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
                                Toast.makeText(this@FullscreenFormActivity,
                                        "Extracted Value: $extractedValue\nFormatted Value: $formattedValue", LENGTH_SHORT).show()
                            }
                        }
                )
            }
            header { title = getString(R.string.FamilyInfo); collapsible = true }
            text(Location.ordinal) {
                title = getString(R.string.Location)
                value = "Dhaka"
                editViewGravity = Gravity.START
                displayDivider = false
                maxLength = 100
                required = true
                enabled = true
                clearable = true
                clearOnFocus = false
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
            }
            textArea(Address.ordinal) {
                title = getString(R.string.Address)
                value = "123 Street"
                editViewGravity = Gravity.START
                displayDivider = false
                minLines = 3
                maxLines = 2
                maxLength = 100
                required = true
                enabled = true
                updateOnFocusChange = true
                imeOptions = EditorInfo.IME_ACTION_DONE
                clearable = true
                clearOnFocus = false
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
            }
            number(ZipCode.ordinal) {
                title = getString(R.string.ZipCode)
                value = 12
                numbersOnly = true
                editViewGravity = Gravity.START
                displayDivider = false
                maxLength = 5
                required = true
                enabled = true
                clearable = false
                clearOnFocus = false
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                    newValue?.let{value -> if(value.toInt() > 10) valueTextColor = Color.RED}
                }
            }
            header { title = getString(R.string.Schedule); collapsible = true }
            date(Tag.Date.ordinal) {
                title = getString(R.string.Date)
                theme = R.style.CustomDialogPicker
                //dateValue = Date()
                dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                minimumDate = dateFormat.parse("01/01/2018")
                maximumDate = dateFormat.parse("12/15/2025")
                startDate = dateFormat.parse("01/02/2018")
                required = true
                maxLines = 1
                confirmEdit = true
                displayDivider = false
                editViewGravity = Gravity.START
                enabled = false
                clearable = true
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
            }
            time(Time.ordinal) {
                title = getString(R.string.Time)
                theme = R.style.CustomDialogPicker
                is24HourView = true
                dateValue = Date()
                dateFormat = SimpleDateFormat("hh:mm a", Locale.US)
                //startDate = dateFormat.parse("12:40 AM")
                required = true
                maxLines = 1
                confirmEdit = true
                displayDivider = false
                editViewGravity = Gravity.START
                enabled = true
                clearable = true
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
            }
            dateTime(DateTime.ordinal) {
                title = getString(R.string.DateTime)
                theme = R.style.CustomDialogPicker
                is24HourView = true
                dateValue = Date()
                dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US)
                //startDate = dateFormat.parse("01/01/2018 12:00 AM")
                minimumDate = dateFormat.parse("01/01/2018 12:00 AM")
                maximumDate = dateFormat.parse("12/15/2025 12:00 PM")
                required = true
                maxLines = 1
                confirmEdit = true
                editViewGravity = Gravity.START
                displayDivider = false
                enabled = true
                clearable = true
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
            }
            header { title = getString(R.string.PreferredItems); collapsible = true }
            radio<Pair<Int, String>>(Radio.ordinal) {
                title = getString(R.string.SingleItem)
                options = fruits
                enabled = true
                editViewGravity = Gravity.START
                theme = R.style.CustomDialogPicker
                displayValueFor = {
                    if (it != null) {
                        it.second + " (" + options?.indexOf(it) + ")"
                    } else {
                        ""
                    }
                }
                displayDivider = false
                setValue(Pair<Int, String>(1, "Banana"))
                required = true
                clearable = true
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                    valueTextColor = when((value as Pair<Int, String>).first){
                        1 -> Color.GREEN
                        3,4 -> Color.YELLOW
                        5 -> Color.RED
                        else -> null
                    }
                }
            }
            dropDown<Pair<Int, String>>(SingleItem.ordinal) {
                title = getString(R.string.SingleItem)
                dialogTitle = getString(R.string.SingleItem)
                options = fruits
                enabled = true
                editViewGravity = Gravity.START
                dialogEmptyMessage = "This is Empty!"
                // dialogTitleCustomView = TextView(this@FullscreenFormActivity)
                theme = R.style.CustomDialogPicker
                displayValueFor = {
                    if (it != null) {
                        it.second + " (" + options?.indexOf(it) + ")"
                    } else {
                        ""
                    }
                }
                confirmEdit = true
                displayRadioButtons = true
                maxLines = 3
                displayDivider = false
                value = Pair<Int, String>(1, "Banana")
                required = true
                clearable = true
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                    valueTextColor = when((value as? Pair<Int, String>?)?.first){
                        1 -> Color.GREEN
                        3,4 -> Color.YELLOW
                        5 -> Color.RED
                        else -> null
                    }
                }
            }
            /*
            multiCheckBox<Pair<Int, String>>(MultiItems.ordinal) {
                title = getString(R.string.MultiItems)
                dialogTitle = getString(R.string.MultiItems)
                theme = R.style.CustomDialogPicker
                options = fruits
                displayValueFor = {
                    if (it != null) {
                        it.second + " (" + it.first + ")"
                    } else {
                        ""
                    }
                }
                enabled = true
                maxLines = 3
                confirmEdit = true
                editViewGravity = Gravity.START
                displayDivider = false
                setListValue( listOf<Pair<Int, String>>(Pair<Int, String>(1, "Banana")))
                required = true
                clearable = true
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                    if (newValue != null) {
                        val list = mutableListOf<Int>()
                        list.add(newValue.first)
                        valueTextColor = when(list.maxBy { it }){
                            1 -> Color.GREEN
                            3,4 -> Color.YELLOW
                            5 -> Color.RED
                            else -> null
                        }
                    }
                }
            }
            */

            autoComplete<ContactItem>(AutoCompleteElement.ordinal) {
                title = getString(R.string.AutoComplete)
                arrayAdapter = ContactAutoCompleteAdapter(this@FullscreenFormActivity,
                        android.R.layout.simple_list_item_1, defaultItems =
                arrayListOf(ContactItem(id = 0, value = "", label = "Try \"Apple May\"")))
                dropdownWidth = ViewGroup.LayoutParams.MATCH_PARENT
                value = ContactItem(id = 1, value = "John Smith", label = "John Smith (Tester)")
                enabled = true
                maxLines = 3
                displayDivider = false
                editViewGravity = Gravity.START
                required = true
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
            }
            autoCompleteToken<List<ContactItem>>(AutoCompleteTokenElement.ordinal) {
                title = getString(R.string.AutoCompleteToken)
                arrayAdapter = EmailAutoCompleteAdapter(this@FullscreenFormActivity,
                        android.R.layout.simple_list_item_1)
                dropdownWidth = ViewGroup.LayoutParams.MATCH_PARENT
                hint = "Try \"Apple May\""
                value = arrayListOf(ContactItem(id = 1, value = "John.Smith@mail.com", label = "John Smith (Tester)"))
                required = true
                maxLines = 3
                editViewGravity = Gravity.START
                displayDivider = false
                enabled = true
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
            }
            textView(TextViewElement.ordinal) {
                title = getString(R.string.TextView)
                editViewGravity = Gravity.START
                maxLines = 1
                value = "This is readonly!"
                displayDivider = false
                editViewPaintFlags = Paint.FAKE_BOLD_TEXT_FLAG
            }
            label(LabelElement.ordinal) {
                title = getString(R.string.Label)
                displayDivider = false
                editViewGravity = Gravity.CENTER
                editViewPaintFlags = Paint.UNDERLINE_TEXT_FLAG
            }
            custom(CustomElement.ordinal){
                title = "Let's test it !"
//                buttonDrawable = getDrawable(R.drawable.ic_add_to_photos_black_24dp)
            }
            header { title = getString(R.string.MarkComplete); collapsible = true }
            switch<String>(SwitchElement.ordinal) {
                title = getString(R.string.Switch)
                value = "Yes"
                onValue = "Yes"
                offValue = "No"
                displayDivider = false
                enabled = true
                required = true
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
            }
            slider(SliderElement.ordinal) {
                title = getString(R.string.Slider)
                value = 37.5f
                min = 36.toFloat()
                max = 42.toFloat()
                displayDivider = false
                incrementBy = 50.0f
                enabled = true
                required = true
                valueObservers.add { newValue, element ->
                    valueTextColor = value?.let { when(it){
                        in "38.5".toDouble().."42".toDouble()-> Color.RED
                        in "37.5".toDouble().."38".toDouble() -> Color.parseColor("#FFFF5722")
                        in min .. "37".toFloat() -> Color.GREEN
                        else -> null
                    }}
                }
            }
            checkBox<Boolean>(CheckBoxElement.ordinal) {
                title = getString(R.string.CheckBox)
                value = true
                checkedValue = true
                unCheckedValue = false
                displayDivider = false
                required = true
                enabled = true
                valueObservers.add { newValue, element ->
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()
                }
            }
            progress(ProgressElement.ordinal) {
                title = getString(R.string.Progress)
                //displayTitle = false
                //centerText = true
                progressBarStyle = FormProgressElement.ProgressBarStyle.HorizontalBar
                indeterminate = false
                progress = 25
                secondaryProgress = 50
                min = 0
                max = 100
                displayDivider = false
            }
            segmented<SegmentedListItem>(SegmentedElement.ordinal) {
                // Set the drawables
                fruitsSegmented[0].drawableRes = R.drawable.icons8_banana
                fruitsSegmented[1].drawableRes = R.drawable.icons8_orange
                fruitsSegmented[2].drawableRes = R.drawable.icons8_avocado
                fruitsSegmented[3].drawableRes = R.drawable.icons8_apple

                title = getString(R.string.Segmented)
                options = fruitsSegmented
                enabled = true
                editViewGravity = Gravity.START
                displayTitle = true
                horizontal = true
                fillSpace = true
                marginDp = 5
                margins = FormElementMargins(16, 16, 16, 0)
                layoutPaddingBottom = 16
                displayDivider = false
                tintColor = Color.parseColor("#FF4081")
                unCheckedTintColor = Color.WHITE
                checkedTextColor = Color.WHITE
                cornerRadius = 0f
                textSize = 12f
                radioGroupWrapContent = false
                radioButtonHeight = 70
                radioButtonPadding = 30
                drawableDirection = FormSegmentedElement.DrawableDirection.Top
                value = fruitsSegmented[0]
                required = true
                valueObservers.add { newValue, element ->
                    options?.forEach {
                        it.height = 50
                        if (it == newValue) {
                            it.height = 70
                        }
                    }
                    Toast.makeText(this@FullscreenFormActivity, newValue.toString(), LENGTH_SHORT).show()

                    reInitGroup()
                }
            }
            button(ButtonElement.ordinal) {
                value = getString(R.string.Button)
                displayDivider = false
                valueBackgroundColor = Color.BLACK
                valueTextColor = Color.WHITE
                enabled = true
                valueObservers.add { newValue, element ->
                    val confirmAlert = AlertDialog.Builder(this@FullscreenFormActivity).create()
                    confirmAlert.setTitle(this@FullscreenFormActivity.getString(R.string.Confirm))
                    confirmAlert.setButton(AlertDialog.BUTTON_POSITIVE, this@FullscreenFormActivity.getString(android.R.string.ok)) { _, _ ->
                        // Could be used to clear another field:
                        val dateToDeleteElement = formBuilder.getFormElement<FormPickerDateElement>(Tag.Date.ordinal)
                        // Display current date
                        Toast.makeText(this@FullscreenFormActivity,
                                dateToDeleteElement.value?.getTime().toString(),
                                Toast.LENGTH_SHORT).show()
                        dateToDeleteElement.clear()
                        formBuilder.onValueChanged(dateToDeleteElement)

                    }
                    confirmAlert.setButton(AlertDialog.BUTTON_NEGATIVE, this@FullscreenFormActivity.getString(android.R.string.cancel)) { _, _ ->
                    }
                    confirmAlert.show()
                }
            }
        }
    }


}