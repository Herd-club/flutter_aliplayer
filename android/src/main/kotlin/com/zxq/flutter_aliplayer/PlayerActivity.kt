package com.zxq.flutter_aliplayer

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.SurfaceView
import com.aliyun.player.AliPlayer
import android.view.View
import android.view.SurfaceHolder
import com.aliyun.player.AliPlayerFactory.createAliPlayer
import com.aliyun.player.source.UrlSource
import com.aliyun.player.source.VidSts
import com.zxq.flutter_aliplayer.activity.AliyunUrlPlayFragment

class PlayerActivity : Activity() {
    var aliyunVodPlayer: AliPlayer? = null
    var surfaceView: SurfaceView? = null
    private val DEFAULT_URL = "http://player.alicdn.com/video/aliyunmedia.mp4"
    private val DEFAULT_VID = "8bb9b7d5c7c64cf49d51fa808b1f0957"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        aliyunVodPlayer = createAliPlayer(getApplicationContext());

        setContentView(R.layout.player)
        surfaceView = findViewById<View>(R.id.play_view) as SurfaceView
        surfaceView!!.getHolder().addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                aliyunVodPlayer?.setDisplay(holder)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                aliyunVodPlayer?.redraw()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                aliyunVodPlayer?.setDisplay(null)
                aliyunVodPlayer?.release()
            }
        })


        val source = UrlSource()
        source.uri = DEFAULT_URL
        aliyunVodPlayer?.setDataSource(source)
        aliyunVodPlayer?.prepare()

        aliyunVodPlayer?.start()
    }
}