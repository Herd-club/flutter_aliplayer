package com.zxq.flutter_aliplayer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

/**
 * Created by lifujun on 2017/9/12.
 * 屏幕开屏/锁屏监听工具类
 */

class ScreenStatusController(private val mContext: Context?) {

    private val TAG = ScreenStatusController::class.java.simpleName
    private var mScreenStatusFilter: IntentFilter? = null
    private var mScreenStatusListener: ScreenStatusListener? = null


    private val mScreenStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_SCREEN_ON == action) { // 开屏
                Log.d(TAG, "ACTION_SCREEN_ON")
                if (mScreenStatusListener != null) {
                    mScreenStatusListener!!.onScreenOn()
                }
            } else if (Intent.ACTION_SCREEN_OFF == action) { // 锁屏
                Log.d(TAG, "ACTION_SCREEN_OFF")
                if (mScreenStatusListener != null) {
                    mScreenStatusListener!!.onScreenOff()
                }
            } else if (Intent.ACTION_USER_PRESENT == action) { // 解锁
            }
        }
    }

    init {

        mScreenStatusFilter = IntentFilter()
        mScreenStatusFilter!!.addAction(Intent.ACTION_SCREEN_ON)
        mScreenStatusFilter!!.addAction(Intent.ACTION_SCREEN_OFF)
        mScreenStatusFilter!!.addAction(Intent.ACTION_USER_PRESENT)
    }


    //监听事件
    interface ScreenStatusListener {
        fun onScreenOn()

        fun onScreenOff()
    }

    //设置监听
    fun setScreenStatusListener(l: ScreenStatusListener) {
        mScreenStatusListener = l
    }

    //开始监听
    fun startListen() {
        mContext?.registerReceiver(mScreenStatusReceiver, mScreenStatusFilter)
    }

    //结束监听
    fun stopListen() {
        mContext?.unregisterReceiver(mScreenStatusReceiver)
    }

}
