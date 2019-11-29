package com.zxq.flutter_aliplayer.view.function

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet

import com.zxq.flutter_aliplayer.R

/**
 * 用于展示视频广告的seekBar
 *
 * @author hanyu
 */
class MutiSeekBarView : android.support.v7.widget.AppCompatSeekBar {

    /**
     * 画笔
     */
    private var mPaint: Paint? = null

    /**
     * View的宽度
     */
    private var mViewWidth: Int = 0

    /**
     * 绘制进度条的Y坐标
     */
    private var mPointY: Int = 0

    /**
     * 视频广告要展示的位置
     */
    private var mAdvPosition: AdvPosition? = null

    /**
     * 广告时长
     */
    private var mAdvTime: Long = 0

    /**
     * 需要添加的广告视频的个数
     */
    private var mAdvNumber: Int = 0

    /**
     * 原视频时长
     */
    private var mSourceTime: Long = 0

    /**
     * 总时长（广告视频+原视频）
     */
    private var mTotalTime: Long = 0

    private var mPaintStrokeWidth = 2

    /**
     * 原视频进度条颜色,默认白色
     */
    private var mSourceSeekColor = resources.getColor(R.color.alivc_common_font_white_light)

    /**
     * 视频进度条颜色,默认蓝色
     */
    private var mAdvSeekColor = resources.getColor(R.color.alivc_player_theme_blue)
    private var mAdvWidth: Int = 0
    private var mSourceWidth: Int = 0
    private var mPaddingRight: Int = 0
    private var mPaddingLeft: Int = 0


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.strokeWidth = mPaintStrokeWidth.toFloat()
        mPaddingLeft = paddingLeft
        mPaddingRight = paddingRight

    }

    /**
     * 设置原视频进度条颜色
     */
    fun setSourceSeekColor(color: Int) {
        this.mSourceSeekColor = color
    }

    /**
     * 设置视频广告进度条颜色
     */
    fun setAdvSeekColor(color: Int) {
        this.mAdvSeekColor = color
    }

    /**
     * 设置时长
     */
    fun setTime(advTime: Long, sourceTime: Long, advPosition: AdvPosition) {
        this.mAdvTime = advTime
        this.mAdvPosition = advPosition
        this.mSourceTime = sourceTime

        when (advPosition) {
            MutiSeekBarView.AdvPosition.ALL -> mAdvNumber = 3
            MutiSeekBarView.AdvPosition.START_END, MutiSeekBarView.AdvPosition.START_MIDDLE, MutiSeekBarView.AdvPosition.MIDDLE_END -> mAdvNumber = 2
            MutiSeekBarView.AdvPosition.ONLY_START, MutiSeekBarView.AdvPosition.ONLY_MIDDLE, MutiSeekBarView.AdvPosition.ONLY_END -> mAdvNumber = 1
            else -> mAdvNumber = 0
        }

        //计算比例
        calculateScale()
        calculateWidth()
        //重新绘制
        invalidate()
    }

    /**
     * 计算比例
     */
    private fun calculateScale() {
        //计算总时长
        mTotalTime = calculateTotal()
    }

    /**
     * 计算视频广告和原视频所需展示的宽度
     */
    fun calculateWidth() {
        if (mTotalTime == 0L) {
            return
        }
        /*
            需要算广告视频的宽度是多少

            广告时长 / 总时长 * 控件的总宽度 = 广告的宽度
         */
        mAdvWidth = ((mViewWidth - mPaddingRight - mPaddingLeft) * mAdvTime / mTotalTime).toInt()
        mSourceWidth = ((mViewWidth - mPaddingRight - mPaddingLeft) * mSourceTime / mTotalTime).toInt()
        invalidate()
    }

    /**
     * 计算总时长,包括视频广告总时长和原视频时长
     */
    private fun calculateTotal(): Long {
        if (mAdvPosition == null) {
            return 0
        }
        val totalTime = mAdvNumber * mAdvTime + mSourceTime
        max = totalTime.toInt()
        setCurrentProgress(0)
        //视频广告时长 * 个数 + 原视频长度
        return mAdvNumber * mAdvTime + mSourceTime
    }

    /**
     * 设置当前进度
     */
    fun setCurrentProgress(currentProgress: Int) {
        progress = currentProgress
    }

    /**
     * 判断当前播放进度是否在起始广告位置
     */
    private fun isVideoPositionInStart(mVideoPosition: Long): Boolean {
        return mVideoPosition >= 0 && mVideoPosition <= mAdvTime
    }

    /**
     * 判断是否进度实在开始位置和中间位置
     * 只适用于 ALL 情况下
     */
    private fun betweenStartAndMiddle(mVideoPosition: Int): Boolean {
        return mVideoPosition > mAdvTime && mVideoPosition < mSourceTime / 2 + mAdvTime
    }

    /**
     * 判断是否进度在中间位置和结束位置
     */
    private fun betweenMiddleAndEnd(mVideoPosition: Int): Boolean {
        return mVideoPosition > mSourceTime / 2 + mAdvTime * 2 && mVideoPosition < mSourceTime + mAdvTime * 2
    }

    /**
     * 判断是否是在中间广告位置之前
     */
    private fun inVideoPositionBeforeMiddle(mVideoPosition: Int): Boolean {
        return if (mAdvPosition == MutiSeekBarView.AdvPosition.ALL || mAdvPosition == MutiSeekBarView.AdvPosition.START_MIDDLE) {
            mVideoPosition >= mSourceTime / 2 + mAdvTime && mVideoPosition <= mSourceTime / 2 + mAdvTime * 2
        } else if (mAdvPosition == AdvPosition.START_END || mAdvPosition == AdvPosition.ONLY_START || mAdvPosition == AdvPosition.ONLY_END) {
            false
        } else {
            //ONLY_MIDDLE,MIDDLE_END
            mVideoPosition >= mSourceTime / 2 && mVideoPosition <= mSourceTime / 2 + mAdvTime
        }
    }

    /**
     * 判断当前播放进度是否在中间广告位置
     */
    private fun isVideoPositionInMiddle(mVideoPosition: Long): Boolean {
        return if (mAdvPosition == MutiSeekBarView.AdvPosition.ALL || mAdvPosition == MutiSeekBarView.AdvPosition.START_MIDDLE) {
            mVideoPosition >= mSourceTime / 2 + mAdvTime && mVideoPosition <= mSourceTime / 2 + mAdvTime * 2
        } else if (mAdvPosition == AdvPosition.START_END || mAdvPosition == AdvPosition.ONLY_START || mAdvPosition == AdvPosition.ONLY_END) {
            false
        } else {
            //ONLY_MIDDLE,MIDDLE_END
            mVideoPosition >= mSourceTime / 2 && mVideoPosition <= mSourceTime / 2 + mAdvTime
        }
    }

    /**
     * 判断当前播放进度是否在结束广告位置
     */
    private fun isVideoPositionInEnd(mVideoPosition: Long): Boolean {
        return if (mAdvPosition == MutiSeekBarView.AdvPosition.ALL || mAdvPosition == MutiSeekBarView.AdvPosition.START_MIDDLE) {
            mVideoPosition >= mSourceTime + mAdvTime * 2
        } else if (mAdvPosition == MutiSeekBarView.AdvPosition.ONLY_START
                || mAdvPosition == MutiSeekBarView.AdvPosition.ONLY_MIDDLE
                || mAdvPosition == MutiSeekBarView.AdvPosition.START_END
                || mAdvPosition == MutiSeekBarView.AdvPosition.MIDDLE_END) {
            mVideoPosition >= mSourceTime + mAdvTime
        } else {
            mVideoPosition >= mSourceTime
        }
    }

    /**
     * 根据目标seek的位置，判断应该从哪个位置开始播放
     *
     * @param seekToPosition 目标seek位置
     * @return 应该播放的seek位置
     */
    fun startPlayPosition(seekToPosition: Long): Long {
        var startPlayPosition = seekToPosition
        when (mAdvPosition) {
            MutiSeekBarView.AdvPosition.ONLY_START -> if (isVideoPositionInStart(seekToPosition)) {
                startPlayPosition = 0
            }
            MutiSeekBarView.AdvPosition.ONLY_MIDDLE -> if (isVideoPositionInMiddle(seekToPosition)) {
                startPlayPosition = mSourceTime / 2
            }
            MutiSeekBarView.AdvPosition.ONLY_END -> if (isVideoPositionInEnd(seekToPosition)) {
                startPlayPosition = mSourceTime
            }
            MutiSeekBarView.AdvPosition.START_END -> if (isVideoPositionInStart(seekToPosition)) {
                startPlayPosition = 0
            } else if (isVideoPositionInEnd(seekToPosition)) {
                startPlayPosition = mSourceTime + mAdvTime
            } else {
                startPlayPosition = seekToPosition
            }
            MutiSeekBarView.AdvPosition.MIDDLE_END -> if (isVideoPositionInMiddle(seekToPosition)) {
                startPlayPosition = mSourceTime / 2
            } else if (isVideoPositionInEnd(seekToPosition)) {
                startPlayPosition = mSourceTime + mAdvTime
            } else {
                startPlayPosition = seekToPosition
            }
            MutiSeekBarView.AdvPosition.START_MIDDLE -> if (isVideoPositionInStart(seekToPosition)) {
                startPlayPosition = 0
            } else if (isVideoPositionInMiddle(seekToPosition)) {
                startPlayPosition = mSourceTime / 2 + mAdvTime
            } else {
                startPlayPosition = seekToPosition
            }
            MutiSeekBarView.AdvPosition.ALL -> if (isVideoPositionInStart(seekToPosition)) {
                startPlayPosition = 0
            } else if (isVideoPositionInMiddle(seekToPosition)) {
                startPlayPosition = mSourceTime / 2 + mAdvTime
            } else if (isVideoPositionInEnd(seekToPosition)) {
                startPlayPosition = mSourceTime + mAdvTime * 2
            } else {
                startPlayPosition = seekToPosition
            }
            else -> {
            }
        }
        return startPlayPosition
    }


    /**
     * 获取seek后需要播放的视频
     */
    fun getIntentPlayVideo(currentPosition: Int, seekToPosition: Int): AdvVideoView.IntentPlayVideo {
        return if (isVideoPositionInStart(seekToPosition.toLong())) {
            AdvVideoView.IntentPlayVideo.START_ADV
        } else if (isVideoPositionInMiddle(seekToPosition.toLong())) {
            AdvVideoView.IntentPlayVideo.MIDDLE_ADV
        } else if (betweenStartAndMiddle(currentPosition) && betweenMiddleAndEnd(seekToPosition)) {
            //起始位置在1，2之间,seekTo到2，3之间的时候
            AdvVideoView.IntentPlayVideo.MIDDLE_ADV_SEEK
        } else if (isVideoPositionInEnd(seekToPosition.toLong()) && betweenMiddleAndEnd(currentPosition)) {
            //起始位置在2，3之间,seekTo到末尾的视频广告处时
            AdvVideoView.IntentPlayVideo.END_ADV
        } else if (betweenStartAndMiddle(currentPosition) && betweenMiddleAndEnd(seekToPosition)) {
            //起始位置是1，2之间，seekTo的位置是2,3之间
            AdvVideoView.IntentPlayVideo.MIDDLE_ADV_SEEK
        } else if (betweenStartAndMiddle(currentPosition) && isVideoPositionInEnd(seekToPosition.toLong())) {
            //起始位置是1，2之间,seekTo的位置是末尾广告位置
            AdvVideoView.IntentPlayVideo.MIDDLE_END_ADV_SEEK
        } else if (betweenStartAndMiddle(seekToPosition) && betweenStartAndMiddle(seekToPosition)) {
            //起始位置是3，4之间,seekTo位置是1,2之间
            AdvVideoView.IntentPlayVideo.REVERSE_SOURCE
        } else {
            AdvVideoView.IntentPlayVideo.NORMAL
        }
    }


    /**
     * 绘制原视频线条
     *
     * @param startX X其实位置
     * @param endX   X的结束位置
     * @param canvas canvas
     */
    private fun drawSourceLine(startX: Int, endX: Int, canvas: Canvas) {
        mPaint!!.color = mSourceSeekColor
        canvas.drawLine(startX.toFloat(), mPointY.toFloat(), endX.toFloat(), mPointY.toFloat(), mPaint!!)
    }

    /**
     * 绘制广告视频线
     *
     * @param startX X其实位置
     * @param endX   X的结束位置
     * @param canvas canvas
     */
    private fun drawAdvLine(startX: Int, endX: Int, canvas: Canvas) {
        mPaint!!.color = mAdvSeekColor
        canvas.drawLine(startX.toFloat(), mPointY.toFloat(), endX.toFloat(), mPointY.toFloat(), mPaint!!)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val mViewHeight = bottom - top
        mViewWidth = right - left


        mPointY = mViewHeight / 2

        calculateWidth()
    }

    override fun onDraw(canvas: Canvas) {
        if (mAdvPosition == null) {
            return
        }
        when (mAdvPosition) {
            MutiSeekBarView.AdvPosition.ONLY_START -> {
                //只有开始位置有广告
                //开始位置绘制广告
                drawAdvLine(mPaddingLeft, mAdvWidth + mPaddingLeft, canvas)
                //再绘制原视频
                drawSourceLine(mAdvWidth + mPaddingLeft, mAdvWidth + mSourceWidth + mPaddingLeft, canvas)
            }
            MutiSeekBarView.AdvPosition.ONLY_MIDDLE -> {
                //只有中间位置有广告
                //开始绘制原视频(一半)
                drawSourceLine(mPaddingLeft, mSourceWidth / 2 + mPaddingLeft, canvas)
                //在中间位置绘制广告
                drawAdvLine(mSourceWidth / 2 + mPaddingLeft, mSourceWidth / 2 + mAdvWidth + mPaddingLeft, canvas)
                //再开始绘制原视频剩下的长度
                drawSourceLine(mSourceWidth / 2 + mAdvWidth + mPaddingLeft, mSourceWidth + mAdvWidth + mPaddingLeft, canvas)
            }
            MutiSeekBarView.AdvPosition.ONLY_END -> {
                //只有结束位置有广告
                //开始绘制原视频
                drawSourceLine(mPaddingLeft, mSourceWidth + mPaddingLeft, canvas)
                //结束时绘制视频广告
                drawAdvLine(mSourceWidth + mPaddingLeft, mSourceWidth + mAdvWidth + mPaddingLeft, canvas)
            }
            MutiSeekBarView.AdvPosition.START_END -> {
                //开始和结束位置有广告
                //开始位置绘制广告视频
                drawAdvLine((x + mPaddingLeft).toInt(), (x + mAdvWidth.toFloat() + mPaddingLeft.toFloat()).toInt(), canvas)
                //绘制原视频
                drawSourceLine(mAdvWidth + mPaddingLeft, mAdvWidth + mSourceWidth + mPaddingLeft, canvas)
                //结束位置绘制广告视频
                drawAdvLine(mAdvWidth + mSourceWidth + mPaddingLeft, mAdvWidth * 2 + mSourceWidth + mPaddingLeft, canvas)
            }
            MutiSeekBarView.AdvPosition.START_MIDDLE -> {
                //开始和中间位置有广告
                //开始位置绘制广告视频
                drawSourceLine(mPaddingLeft, mAdvWidth + mPaddingLeft, canvas)
                //绘制原视频的一半
                drawSourceLine(mAdvWidth + mPaddingLeft, mAdvWidth + mSourceWidth / 2 + mPaddingLeft, canvas)
                //绘制广告视频
                drawAdvLine(mAdvWidth + mSourceWidth / 2 + mPaddingLeft, mAdvWidth * 2 + mSourceWidth / 2 + mPaddingLeft, canvas)
                //绘制原视频的另一半
                drawSourceLine(mAdvWidth * 2 + mSourceWidth / 2 + mPaddingLeft, mAdvWidth * 2 + mSourceWidth + mPaddingLeft, canvas)
            }
            MutiSeekBarView.AdvPosition.MIDDLE_END -> {
                //中间和结束位置有广告
                //开始绘制原视频的一半
                drawSourceLine(mPaddingLeft, mSourceWidth / 2 + mPaddingLeft, canvas)
                //中间位置绘制广告
                drawAdvLine(mSourceWidth / 2 + mPaddingLeft, mSourceWidth / 2 + mAdvWidth + mPaddingLeft, canvas)
                //绘制原视频的另一半
                drawSourceLine(mSourceWidth / 2 + mAdvWidth + mPaddingLeft, mSourceWidth + mAdvWidth + mPaddingLeft, canvas)
                //结束位置绘制视频广告
                drawAdvLine(mSourceWidth + mAdvWidth + mPaddingLeft, mSourceWidth + mAdvWidth * 2 + mPaddingLeft, canvas)
            }
            MutiSeekBarView.AdvPosition.ALL -> {
                //开始和中间和结束位置都有视频广告
                //开始位置绘制广告
                drawAdvLine(mPaddingLeft, mAdvWidth + mPaddingLeft, canvas)
                //绘制原视频的一半
                drawSourceLine(mAdvWidth + mPaddingLeft, mAdvWidth + mSourceWidth / 2 + mPaddingLeft, canvas)
                //在中间位置绘制广告视频
                drawAdvLine(mAdvWidth + mSourceWidth / 2 + mPaddingLeft, mAdvWidth * 2 + mSourceWidth / 2 + mPaddingLeft, canvas)
                //绘制原视频的另一半
                drawSourceLine(mAdvWidth * 2 + mSourceWidth / 2 + mPaddingLeft, mAdvWidth * 2 + mSourceWidth + mPaddingLeft, canvas)
                //在结束位置绘制广告视频
                drawAdvLine(mAdvWidth * 2 + mSourceWidth + mPaddingLeft, mAdvWidth * 3 + mSourceWidth + mPaddingLeft, canvas)
            }
            else ->
                //没有视频广告
                drawSourceLine(mPaddingLeft, mSourceWidth, canvas)
        }
        super.onDraw(canvas)
    }


    /**
     * 视频广告位置
     */
    enum class AdvPosition private constructor(n: Int) {
        /**
         * 开始位置
         */
        ONLY_START(0),
        /**
         * 中间位置
         */
        ONLY_MIDDLE(1),
        /**
         * 结束位置
         */
        ONLY_END(2),
        /**
         * 开始和结束位置
         */
        START_END(3),
        /**
         * 开始和中间位置
         */
        START_MIDDLE(4),
        /**
         * 中间和结束位置
         */
        MIDDLE_END(5),
        /**
         * 所有
         */
        ALL(6)
    }
}
