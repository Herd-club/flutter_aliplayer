package com.zxq.flutter_aliplayer.theme

import com.zxq.flutter_aliplayer.view.tipsview.ErrorView
import com.zxq.flutter_aliplayer.view.tipsview.NetChangeView
import com.zxq.flutter_aliplayer.view.tipsview.ReplayView
import com.zxq.flutter_aliplayer.widget.AliyunVodPlayerView
import com.zxq.flutter_aliplayer.view.control.ControlView
import com.zxq.flutter_aliplayer.view.guide.GuideView
import com.zxq.flutter_aliplayer.view.quality.QualityView
import com.zxq.flutter_aliplayer.view.speed.SpeedView
import com.zxq.flutter_aliplayer.view.tipsview.TipsView

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 主题的接口。用于变换UI的主题。
 * 实现类有[ErrorView]，[NetChangeView] , [ReplayView] ,[ControlView],
 * [GuideView] , [QualityView], [SpeedView] , [TipsView],
 * [AliyunVodPlayerView]
 */

interface ITheme {
    /**
     * 设置主题
     * @param theme 支持的主题
     */
    fun setTheme(theme: AliyunVodPlayerView.Theme)
}
