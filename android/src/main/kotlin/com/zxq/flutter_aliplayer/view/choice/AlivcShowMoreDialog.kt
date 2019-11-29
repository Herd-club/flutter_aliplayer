package com.zxq.flutter_aliplayer.view.choice

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation

import com.zxq.flutter_aliplayer.R
import com.zxq.flutter_aliplayer.utils.ScreenUtils
import com.zxq.flutter_aliplayer.widget.AliyunScreenMode

import java.lang.ref.WeakReference

class AlivcShowMoreDialog @JvmOverloads constructor(context: Context, private val screenMode: AliyunScreenMode = AliyunScreenMode.Full) : Dialog(context, R.style.addDownloadDialog) {
    private var mContentView: View? = null
    private val activityWeakReference: WeakReference<Context>
    private var mIsAnimating = false


    init {
        activityWeakReference = WeakReference(context)
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        window!!.decorView.setPadding(0, 0, 0, 0)
        //// 在底部，宽度撑满
        //WindowManager.LayoutParams params = getWindow().getAttributes();
        //params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        //params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        //
        //int screenWidth = ScreenUtils.getWidth(activityWeakReference.get());
        //int screenHeight = ScreenUtils.getHeight(activityWeakReference.get());
        //params.width = screenWidth < screenHeight ? screenWidth : screenHeight;
        //getWindow().setAttributes(params);
        //setCanceledOnTouchOutside(true);
        setLayoutBySreenMode(screenMode)
    }

    override fun setContentView(view: View) {
        mContentView = view
        super.setContentView(view)
    }


    fun setLayoutBySreenMode(aliyunScreenMode: AliyunScreenMode) {
        if (aliyunScreenMode == AliyunScreenMode.Small) {
            val params = window!!.attributes
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.BOTTOM or Gravity.CENTER

            val screenWidth = ScreenUtils.getWidth(context)
            val screenHeight = ScreenUtils.getHeight(context)
            params.width = if (screenWidth < screenHeight) screenWidth else screenHeight
            window!!.attributes = params
            setCanceledOnTouchOutside(true)
        } else {
            val params = window!!.attributes
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.gravity = Gravity.RIGHT

            val screenWidth = ScreenUtils.getWidth(context)
            val screenHeight = ScreenUtils.getHeight(context)
            params.width = if (screenWidth < screenHeight) screenWidth else screenHeight
            window!!.attributes = params
            setCanceledOnTouchOutside(true)
        }
    }

    /**
     * ChoiceItemBottomDialog从下往上升起的动画动画
     */
    private fun animateUp() {
        if (mContentView != null) {
            return
        }
        val translateAnimation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f)
        val alphaAnimation = AlphaAnimation(0f, 1f)
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(translateAnimation)
        animationSet.addAnimation(alphaAnimation)
        animationSet.interpolator = DecelerateInterpolator()
        animationSet.duration = ANIMATION_DURATION.toLong()
        animationSet.fillAfter = true
        mContentView!!.startAnimation(animationSet)
    }

    /**
     * ChoiceItemBottomDialog从下往上升起的动画动画
     */
    private fun animateDown() {
        if (mContentView == null) {
            return
        }
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f
        )
        val alpha = AlphaAnimation(1f, 0f)
        val set = AnimationSet(true)
        set.addAnimation(translate)
        set.addAnimation(alpha)
        set.interpolator = DecelerateInterpolator()
        set.duration = ANIMATION_DURATION.toLong()
        set.fillAfter = true
        set.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                mIsAnimating = true
            }

            override fun onAnimationEnd(animation: Animation) {
                mIsAnimating = false
                mContentView!!.post {
                    try {
                        super@AlivcShowMoreDialog.dismiss()
                    } catch (e: Exception) {
                        Log.w("Test", "dismiss error\n" + Log.getStackTraceString(e))
                    }
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        mContentView!!.startAnimation(set)
    }

    override fun show() {
        super.show()
        animateUp()
    }

    override fun dismiss() {
        super.dismiss()
        animateDown()
    }

    companion object {
        private val ANIMATION_DURATION = 200
    }
}
