package com.zxq.flutter_aliplayer.view.quality

import android.content.Context

import com.zxq.flutter_aliplayer.R

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 清晰度列表的项
 */
class QualityItem private constructor(//原始的清晰度
        private val mQuality: String, //显示的文字
        /**
         * 获取显示的文字
         *
         * @return 清晰度文字
         */
        val name: String) {
    companion object {

        fun getItem(context: Context, quality: String, isMts: Boolean): QualityItem {
            //mts与其他的清晰度格式不一样，
            if (isMts) {
                //这里是getMtsLanguage
                val name = QualityLanguage.getMtsLanguage(context, quality)
                return QualityItem(quality, name!!)
            } else {
                //这里是getSaasLanguage
                val name = QualityLanguage.getSaasLanguage(context, quality)
                return QualityItem(quality, name)
            }
        }
    }

}
