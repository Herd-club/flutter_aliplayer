package com.zxq.flutter_aliplayer.view.choice

import java.util.ArrayList

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.TextView
import com.zxq.flutter_aliplayer.R
import com.zxq.flutter_aliplayer.utils.ScreenUtils

/**
 * @author Mulberry
 * create on 2018/5/15.
 */

class AlivcActionListDialog : Dialog {
    private var mContentView: View? = null
    private var mIsAnimating = false


    internal var onChoiceItemListener: OnChoiceItemListener? = null

    constructor(context: Context) : super(context, R.style.BottomCheckDialog) {}

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {}

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        window!!.decorView.setPadding(0, 0, 0, 0)

        val params = window!!.attributes
        params.height = LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.BOTTOM or Gravity.CENTER

        val screenWidth = ScreenUtils.getWidth(context)
        val screenHeight = ScreenUtils.getHeight(context)
        params.width = if (screenWidth < screenHeight) screenWidth else screenHeight
        window!!.attributes = params
        setCanceledOnTouchOutside(true)
    }

    override fun setContentView(layoutResID: Int) {
        mContentView = LayoutInflater.from(context).inflate(layoutResID, null)
        super.setContentView(layoutResID)
    }

    override fun setContentView(view: View, params: LayoutParams?) {
        mContentView = view
        super.setContentView(view, params)
    }

    fun getContentView(): View? {
        return mContentView
    }

    override fun setContentView(mContentView: View) {
        this.mContentView = mContentView
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
                        super@AlivcActionListDialog.dismiss()
                    } catch (e: Exception) {
                        Log.w(TAG, "dismiss error\n" + Log.getStackTraceString(e))
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
        if (onChoiceItemListener != null) {
            onChoiceItemListener!!.onShow()
        }
    }

    override fun dismiss() {
        super.dismiss()
        if (mIsAnimating) {
            return
        }
        animateDown()
    }

    fun setOnChoiceItemListener(
            onChoiceButtomItemListener: OnChoiceItemListener) {
        this.onChoiceItemListener = onChoiceButtomItemListener
    }

    interface OnChoiceItemListener {
        /**
         * 显示
         */
        fun onShow()
    }

    /**
     * 生成底部选择[AlivcActionListDialog]对话框
     */
    class BottomListCheckBuilder(private val mContext: Context) {
        private var alivcCheckItemDialog: AlivcActionListDialog? = null
        private val mItems: MutableList<BottomCheckListItemData>
        private val recyclerView: RecyclerView? = null
        private var mCheckedIndex: Int = 0

        private var onCheckItemClickListener: OnCheckItemClickListener? = null
        private var onBottomDialogDismissListener: DialogInterface.OnDismissListener? = null

        /**
         * 注意:这里只考虑List的高度,如果有title或者headerView,不计入考虑中
         */
        protected val listMaxHeight: Int
            get() = (ScreenUtils.getHeight(mContext) * 0.5).toInt()

        protected val contentViewLayoutId: Int
            get() = R.layout.alivc_check_list_view_layout

        init {
            mItems = ArrayList()
        }

        /**
         * 设置要被选择的item的下标
         * @param mCheckedIndex
         * @return
         */
        fun setCheckedIndex(mCheckedIndex: Int): BottomListCheckBuilder {
            this.mCheckedIndex = mCheckedIndex
            return this
        }

        /**
         * @param typeAndTag Item 的文字内容，同时会把内容设置为 tag。
         */
        fun addItem(typeAndTag: String, value: String): BottomListCheckBuilder {
            mItems.add(BottomCheckListItemData(typeAndTag, value, typeAndTag))
            return this
        }

        /**
         * 设置item点击事件
         * @param onCheckItemClickListener
         * @return
         */
        fun setOnCheckItemClickListener(
                onCheckItemClickListener: OnCheckItemClickListener): BottomListCheckBuilder {
            this.onCheckItemClickListener = onCheckItemClickListener
            return this
        }

        /**
         * dialog dismiss添加回调
         * @param onBottomDialogDismissListener
         * @return
         */
        fun setOnBottomDialogDismissListener(
                onBottomDialogDismissListener: DialogInterface.OnDismissListener): BottomListCheckBuilder {
            this.onBottomDialogDismissListener = onBottomDialogDismissListener
            return this
        }

        /**
         * 构建一个AlivcCheckItemDialog
         * @return
         */
        fun build(): AlivcActionListDialog {
            alivcCheckItemDialog = AlivcActionListDialog(mContext)
            val contentView = buildViews()
            alivcCheckItemDialog!!.setContentView(contentView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            if (onBottomDialogDismissListener != null) {
                alivcCheckItemDialog!!.setOnDismissListener(onBottomDialogDismissListener)
            }
            return alivcCheckItemDialog as AlivcActionListDialog
        }

        private fun buildViews(): View {
            val wrapperView = View.inflate(mContext, contentViewLayoutId, null)
            val tvCloseBottomCheck = wrapperView.findViewById<View>(R.id.tv_close_bottom_check) as TextView
            val mContainerView = wrapperView.findViewById<View>(R.id.check_list_view) as RecyclerView

            tvCloseBottomCheck.setOnClickListener {
                if (alivcCheckItemDialog != null) {
                    alivcCheckItemDialog!!.dismiss()
                }
            }
            //if (needToScroll()) {
            mContainerView.layoutParams.height = listMaxHeight
            alivcCheckItemDialog!!.setOnChoiceItemListener(object : OnChoiceItemListener {
                override fun onShow() {
                    //onshow do Something
                }
            })
            //}

            mContainerView.layoutManager = LinearLayoutManager(mContext)
            val mAdapter = CheckListAdapter()
            mContainerView.adapter = mAdapter
            return wrapperView
        }

        interface OnCheckItemClickListener {
            fun onClick(dialog: AlivcActionListDialog?, itemView: View, position: Int, tag: String)
        }

        private class BottomCheckListItemData(internal var type: String, internal var value: String, internal var tag: String)

        inner class CheckListAdapter : RecyclerView.Adapter<CheckListAdapter.ViewHolder>() {

            inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                internal var bottomDialogListItem: LinearLayout
                internal var type: TextView
                internal var value: TextView

                init {
                    bottomDialogListItem = itemView.findViewById<View>(R.id.bottom_dialog_list_item) as LinearLayout
                    type = itemView.findViewById<View>(R.id.bottom_dialog_list_item_type) as TextView
                    value = itemView.findViewById<View>(R.id.bottom_dialog_list_item_value) as TextView
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(
                        LayoutInflater.from(parent.context).inflate(R.layout.alivc_check_list_item, parent, false))
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.type.text = mItems[position].type
                holder.value.text = mItems[position].value

                holder.bottomDialogListItem.setOnClickListener { v -> onCheckItemClickListener!!.onClick(alivcCheckItemDialog, v, position, mItems[position].tag) }
            }

            override fun getItemCount(): Int {
                return mItems.size
            }

            override fun getItemViewType(position: Int): Int {
                return super.getItemViewType(position)
            }

        }


    }

    companion object {

        private val TAG = AlivcActionListDialog::class.java.name

        private val ANIMATION_DURATION = 200
    }


}
