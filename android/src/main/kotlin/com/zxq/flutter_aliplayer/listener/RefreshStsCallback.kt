package com.zxq.flutter_aliplayer.listener

import com.aliyun.player.source.VidSts

/**
 * sts刷新回调
 */
interface RefreshStsCallback {

    fun refreshSts(vid: String, quality: String, format: String, title: String, encript: Boolean): VidSts?
}
