package com.zxq.flutter_aliplayer.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * Created by lifujun on 2017/6/22.
 */

open class WordWrapView : ViewGroup {

    /**
     * @param context
     */
    constructor(context: Context) : super(context) {}

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}


    /**
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        val autualWidth = r - l
        var x = SIDE_MARGIN// 横坐标开始
        var y = 0//纵坐标开始
        var rows = 1
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            val width = view.measuredWidth
            val height = view.measuredHeight
            x += width + TEXT_MARGIN
            if (x > autualWidth) {
                x = width + SIDE_MARGIN
                rows++
            }
            y = rows * (height + TEXT_MARGIN)
            if (i == 0) {
                view.layout(x - width - TEXT_MARGIN, y - height, x - TEXT_MARGIN, y)
            } else {
                view.layout(x - width, y - height, x, y)
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var x = 0//横坐标
        var y = 0//纵坐标
        var rows = 1//总行数
        val specWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val actualWidth = specWidth - SIDE_MARGIN * 2//实际宽度
        val childCount = childCount
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            child.setPadding(PADDING_HOR, PADDING_VERTICAL, PADDING_HOR, PADDING_VERTICAL)
            child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val width = child.measuredWidth
            val height = child.measuredHeight
            x += width + TEXT_MARGIN
            if (x > actualWidth) {//换行
                x = width
                rows++
            }
            y = rows * (height + TEXT_MARGIN)
        }
        setMeasuredDimension(actualWidth, y)
    }

    companion object {
        private val PADDING_HOR = 10//水平方向padding
        private val PADDING_VERTICAL = 5//垂直方向padding
        private val SIDE_MARGIN = 10//左右间距
        private val TEXT_MARGIN = 10
    }


}
