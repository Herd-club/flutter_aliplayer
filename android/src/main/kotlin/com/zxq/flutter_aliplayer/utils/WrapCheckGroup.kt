package com.zxq.flutter_aliplayer.utils

import java.util.LinkedList

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CheckBox

/**
 * Created by lifujun on 2017/6/22.
 */

class WrapCheckGroup : WordWrapView {

    internal var childs: MutableList<CheckBox> = LinkedList()

    var clickListener: View.OnClickListener = OnClickListener { v ->
        val isChecked = (v as CheckBox).isChecked
        clearAllCheck()
        v.isChecked = isChecked
    }

    val selectedBox: CheckBox?
        get() {
            for (checkBox in childs) {
                if (checkBox.isChecked) {
                    return checkBox
                }
            }
            return null
        }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    fun addCheckBox(checkBox: CheckBox) {
        checkBox.isChecked = false
        checkBox.setOnClickListener(clickListener)
        childs.add(checkBox)
        addView(checkBox)
    }

    private fun clearAllCheck() {
        for (checkBox in childs) {
            if (checkBox.isChecked) {
                checkBox.isChecked = false
            }
        }
    }

    fun removeCheckBox() {
        childs.clear()
        removeAllViews()
    }
}
