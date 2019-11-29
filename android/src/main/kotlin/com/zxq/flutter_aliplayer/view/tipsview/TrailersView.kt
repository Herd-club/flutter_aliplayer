package com.zxq.flutter_aliplayer.view.tipsview

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.zxq.flutter_aliplayer.R

/**
 * 试看
 * @author hanyu
 */
class TrailersView : RelativeLayout {

    private var mTipsTextView: TextView? = null
    private var view: View? = null
    private var mTrailersMask: FrameLayout? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val inflater = context.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.alivc_trailers_view_layout, this, true)


        initView()
    }

    private fun initView() {
        mTipsTextView = view!!.findViewById(R.id.tv_tips)
        mTrailersMask = view!!.findViewById(R.id.trailers_mask)
    }

    fun setContentText(text: String) {
        if (mTipsTextView != null) {
            mTipsTextView!!.text = text
        }
    }

    /**
     * 超过了试看时长
     */
    fun setCurrentProgress(overTrailerTime: Boolean) {
        if (overTrailerTime) {
            visibility = View.VISIBLE
            mTrailersMask!!.visibility = View.VISIBLE
            mTipsTextView!!.visibility = View.VISIBLE
            mTipsTextView!!.text = context.getString(R.string.alivc_tips_trailer_end)
        } else {
            if (mTrailersMask!!.isShown) {
                mTrailersMask!!.visibility = View.INVISIBLE
            }
            if (mTipsTextView!!.isShown) {
                mTipsTextView!!.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * 隐藏所有
     */
    fun hideAll() {
        mTrailersMask!!.visibility = View.INVISIBLE
        mTipsTextView!!.visibility = View.GONE
    }

    /**
     * 开始试看
     */
    fun startTrailer() {
        if (mTrailersMask != null) {
            mTrailersMask!!.visibility = View.INVISIBLE
        }
        if (mTipsTextView != null) {
            mTipsTextView!!.visibility = View.VISIBLE
            mTipsTextView!!.text = context.getString(R.string.alivc_tips_trailer)
        }
    }

    /**
     * 试看结束
     */
    fun endTrailer() {
        if (mTrailersMask != null) {
            mTrailersMask!!.visibility = View.VISIBLE
        }
        if (mTipsTextView != null) {
            mTipsTextView!!.visibility = View.VISIBLE
            mTipsTextView!!.text = context.getString(R.string.alivc_tips_trailer_end)
        }
    }

}
