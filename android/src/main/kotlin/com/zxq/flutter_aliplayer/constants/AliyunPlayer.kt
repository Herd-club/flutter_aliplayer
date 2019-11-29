package com.zxq.flutter_aliplayer.constants

import android.content.Context
import android.content.Intent

import com.zxq.flutter_aliplayer.activity.AliyunPlayerSkinActivity

/**
 * 对外暴露的接口
 */
object AliyunPlayer {

    /**
     * 跳转到播放器界面
     * @param context               context
     * @param alivcPlayerConfig     跳转到播放器提供的参数配置
     */
    fun player(context: Context, alivcPlayerConfig: AlivcPlayerConfig) {
        val intent = Intent(context, AliyunPlayerSkinActivity::class.java)
        intent.putExtra("vid", alivcPlayerConfig.vid)
        context.startActivity(intent)
    }
}
