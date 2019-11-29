package com.zxq.flutter_aliplayer.listener

import com.zxq.flutter_aliplayer.R

/**
 * 清晰度切换监听
 */
interface OnChangeQualityListener {

    fun onChangeQualitySuccess(quality: String)

    fun onChangeQualityFail(code: Int, msg: String)
}
