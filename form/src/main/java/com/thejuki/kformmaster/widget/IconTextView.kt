package com.thejuki.kformmaster.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.widget.AppCompatTextView

/**
 * Icon TextView
 *
 * Adds an icon to the TextView with an onClickListener.
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class IconTextView : AppCompatTextView, OnTouchListener {
    var leftIcon: Drawable? = null
    var rightIcon: Drawable? = null
    var iconPadding: Int = 20

    var listener: Listener? = null

    private var onIconTouchListener: OnTouchListener? = null

    interface Listener {
        fun clickedIcon()
    }

    @SuppressLint("ClickableViewAccessibility")
    constructor(context: Context) : super(context) {
        super.setOnTouchListener(this)
    }
    @SuppressLint("ClickableViewAccessibility")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        super.setOnTouchListener(this)
    }
    @SuppressLint("ClickableViewAccessibility")
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        super.setOnTouchListener(this)
    }

    override fun setOnTouchListener(onTouchListener: OnTouchListener) {
        this.onIconTouchListener = onTouchListener
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (leftIcon != null || rightIcon != null) {
            val x = event.x.toInt()
            val y = event.y.toInt()

            val rightSideOfLeftIcon = paddingLeft + (leftIcon?.intrinsicWidth ?: 0)
            val leftIconTapped = x in 0..rightSideOfLeftIcon && y >= 0 && y <= bottom - top

            val leftSideOfRightIcon = width - paddingRight - (rightIcon?.intrinsicWidth ?: 0)
            val rightIconTapped = x in leftSideOfRightIcon..width && y >= 0 && y <= bottom - top

            if (leftIconTapped || rightIconTapped) {
                if (event.action == MotionEvent.ACTION_UP) {
                    listener?.clickedIcon()
                }
                return true
            }
        }
        return onIconTouchListener?.onTouch(v, event) == true
    }

    override fun setCompoundDrawables(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
        super.setCompoundDrawables(left, top, right, bottom)
        initIcon()
    }

    fun reInitIcon() {
        initIcon()
        setIconVisible()
    }

    private val leftIconWidth: Int
        get() = leftIcon?.intrinsicWidth ?: 0

    private val leftIconHeight: Int
        get() = leftIcon?.intrinsicHeight ?: 0

    private val rightIconHeight: Int
        get() = rightIcon?.intrinsicHeight ?: 0

    private val rightIconWidth: Int
        get() = rightIcon?.intrinsicWidth ?: 0

    private fun initIcon() {
        leftIcon?.setBounds(0, 0, leftIconWidth, leftIconHeight)
        rightIcon?.setBounds(0, 0, rightIconWidth, rightIconHeight)
        val min = paddingTop + leftIconHeight.coerceAtLeast(rightIconHeight) + paddingBottom
        if (suggestedMinimumHeight < min) {
            minimumHeight = min
        }
    }

    private fun setIconVisible() {
        val cd = compoundDrawables
        if (leftIcon == null) {
            leftIcon = cd[0]
        }
        if (rightIcon == null) {
            rightIcon = cd[2]
        }
        super.setCompoundDrawables(leftIcon, cd[1], rightIcon, cd[3])
        super.setCompoundDrawablePadding(iconPadding)
    }
}