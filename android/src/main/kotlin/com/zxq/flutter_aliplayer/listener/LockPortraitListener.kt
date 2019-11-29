package com.zxq.flutter_aliplayer.listener

/**
 * 屏幕状态监听
 */
interface LockPortraitListener {

    fun onLockScreenMode(type: Int)

    companion object {

        val FIX_MODE_SMALL = 1
        val FIX_MODE_FULL = 2
    }
}
