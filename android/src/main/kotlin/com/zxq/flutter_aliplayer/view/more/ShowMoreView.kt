package com.zxq.flutter_aliplayer.view.more

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView

import com.zxq.flutter_aliplayer.R
import com.zxq.flutter_aliplayer.activity.AliyunPlayerSkinActivity
import com.zxq.flutter_aliplayer.widget.AliyunScreenMode

class ShowMoreView(context: AliyunPlayerSkinActivity, private val moreValue: AliyunShowMoreValue?) : LinearLayout(context), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private var seekLight: SeekBar? = null
    private var seekVoice: SeekBar? = null
    private var tvDonwload: TextView? = null
    private var tvCastScreen: TextView? = null
    private var tvBarrage: TextView? = null
    private var rgSpeed: RadioGroup? = null
    private var mOnDownloadButtonClickListener: OnDownloadButtonClickListener? = null
    private var mOnSpeedCheckedChangedListener: OnSpeedCheckedChangedListener? = null
    private var mOnLightSeekChangeListener: OnLightSeekChangeListener? = null
    private var mOnVoiceSeekChangeListener: OnVoiceSeekChangeListener? = null
    private var mOnScreenCastButtonClickListener: OnScreenCastButtonClickListener? = null
    private var mOnBarrageButtonClickListener: OnBarrageButtonClickListener? = null

    init {
        init()
    }

    private fun init() {
        val view = LayoutInflater.from(context).inflate(R.layout.alivc_dialog_more, this, true)
        findAllViews(view)
    }

    private fun findAllViews(view: View) {
        seekLight = view.findViewById(R.id.seek_light)
        seekVoice = view.findViewById(R.id.seek_voice)
        tvDonwload = view.findViewById(R.id.tv_download)
        tvCastScreen = view.findViewById(R.id.tv_cast_screen)
        tvBarrage = view.findViewById(R.id.tv_barrage)
        rgSpeed = findViewById(R.id.alivc_rg_speed)
        configViews()
        addListener()

    }

    private fun configViews() {
        if (moreValue == null) {
            return
        }
        seekLight!!.progress = moreValue.screenBrightness
        seekVoice!!.progress = moreValue.volume

        var currentRbIndex = 0
        val curentSpeed = moreValue.speed
        if (curentSpeed == 1.0f) {
            currentRbIndex = 0
        } else if (curentSpeed == 1.25f) {
            currentRbIndex = 1
        } else if (curentSpeed == 1.5f) {
            currentRbIndex = 2
        } else if (curentSpeed == 2.0f) {
            currentRbIndex = 3
        }
        rgSpeed!!.check(rgSpeed!!.getChildAt(currentRbIndex).id)
    }


    private fun addListener() {
        tvDonwload!!.setOnClickListener(this)
        tvCastScreen!!.setOnClickListener(this)
        tvBarrage!!.setOnClickListener(this)

        rgSpeed!!.setOnCheckedChangeListener(this)

        seekLight!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                if (mOnLightSeekChangeListener != null) {
                    mOnLightSeekChangeListener!!.onStart(seekBar)
                }
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mOnLightSeekChangeListener != null) {
                    mOnLightSeekChangeListener!!.onProgress(seekBar, progress, fromUser)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mOnLightSeekChangeListener != null) {
                    mOnLightSeekChangeListener!!.onStop(seekBar)
                }
            }
        })

        seekVoice!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                if (mOnVoiceSeekChangeListener != null) {
                    mOnVoiceSeekChangeListener!!.onStart(seekBar)
                }
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mOnVoiceSeekChangeListener != null) {
                    mOnVoiceSeekChangeListener!!.onProgress(seekBar, progress, fromUser)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mOnVoiceSeekChangeListener != null) {
                    mOnVoiceSeekChangeListener!!.onStop(seekBar)
                }
            }
        })
    }


    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.tv_download) {
            // 下载
            if (mOnDownloadButtonClickListener != null) {
                mOnDownloadButtonClickListener!!.onDownloadClick()
            }
        } else if (id == R.id.tv_cast_screen) {
            // 投屏
            if (mOnScreenCastButtonClickListener != null) {
                mOnScreenCastButtonClickListener!!.onScreenCastClick()
            }

        } else if (id == R.id.tv_barrage) {
            // 弹幕
            if (mOnBarrageButtonClickListener != null) {
                mOnBarrageButtonClickListener!!.onBarrageClick()
            }
        }

    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        if (mOnSpeedCheckedChangedListener != null) {
            mOnSpeedCheckedChangedListener!!.onSpeedChanged(group, checkedId)
        }
    }

    interface OnDownloadButtonClickListener {
        /**
         * 下载按钮点击
         */
        fun onDownloadClick()
    }

    fun setOnDownloadButtonClickListener(listener: OnDownloadButtonClickListener) {
        this.mOnDownloadButtonClickListener = listener
    }

    interface OnScreenCastButtonClickListener {
        /**
         * 投屏按钮点击
         */
        fun onScreenCastClick()
    }

    fun setOnScreenCastButtonClickListener(listener: OnScreenCastButtonClickListener) {
        this.mOnScreenCastButtonClickListener = listener
    }

    interface OnBarrageButtonClickListener {
        /**
         * 弹幕按钮点击
         */
        fun onBarrageClick()
    }

    fun setOnBarrageButtonClickListener(listener: OnBarrageButtonClickListener) {
        this.mOnBarrageButtonClickListener = listener
    }

    interface OnSpeedCheckedChangedListener {
        /**
         * 速度切换
         * @param group
         * @param checkedId
         */
        fun onSpeedChanged(group: RadioGroup, checkedId: Int)
    }

    fun setOnSpeedCheckedChangedListener(listener: OnSpeedCheckedChangedListener) {
        this.mOnSpeedCheckedChangedListener = listener
    }

    /**
     * 亮度调节
     */
    interface OnLightSeekChangeListener {
        fun onStart(seekBar: SeekBar)
        fun onProgress(seekBar: SeekBar, progress: Int, fromUser: Boolean)
        fun onStop(seekBar: SeekBar)
    }

    fun setOnLightSeekChangeListener(listener: OnLightSeekChangeListener) {
        this.mOnLightSeekChangeListener = listener
    }

    /**
     * 音量调节
     */
    interface OnVoiceSeekChangeListener {
        fun onStart(seekBar: SeekBar)
        fun onProgress(seekBar: SeekBar, progress: Int, fromUser: Boolean)
        fun onStop(seekBar: SeekBar)
    }

    fun setOnVoiceSeekChangeListener(listener: OnVoiceSeekChangeListener) {
        this.mOnVoiceSeekChangeListener = listener
    }

    /**
     * 设置音量
     */
    fun setVoiceVolume(volume: Float) {
        if (seekVoice != null) {
            seekVoice!!.progress = (volume * 100).toInt()
        }
    }

    /**
     * 设置亮度
     */
    fun setBrightness(value: Int) {
        if (seekLight != null) {
            seekLight!!.progress = value
        }
    }
}

