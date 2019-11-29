package com.zxq.flutter_aliplayer.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import com.aliyun.player.IPlayer
import com.aliyun.player.bean.ErrorCode
import com.aliyun.player.source.UrlSource
import com.aliyun.player.source.VidSts
import com.aliyun.utils.VcPlayerLog
import com.zxq.flutter_aliplayer.R
import com.zxq.flutter_aliplayer.constants.PlayParameter
import com.zxq.flutter_aliplayer.listener.OnChangeQualityListener
import com.zxq.flutter_aliplayer.listener.OnStoppedListener
import com.zxq.flutter_aliplayer.listener.RefreshStsCallback
import com.zxq.flutter_aliplayer.utils.Common
import com.zxq.flutter_aliplayer.utils.FixedToastUtils
import com.zxq.flutter_aliplayer.utils.ScreenUtils
import com.zxq.flutter_aliplayer.utils.VidStsUtil
import com.zxq.flutter_aliplayer.view.choice.AlivcShowMoreDialog
import com.zxq.flutter_aliplayer.view.control.ControlView
import com.zxq.flutter_aliplayer.view.gesturedialog.BrightnessDialog
import com.zxq.flutter_aliplayer.view.more.AliyunShowMoreValue
import com.zxq.flutter_aliplayer.view.more.ShowMoreView
import com.zxq.flutter_aliplayer.view.more.SpeedValue
import com.zxq.flutter_aliplayer.view.tipsview.ErrorInfo
import com.zxq.flutter_aliplayer.widget.AliyunScreenMode
import com.zxq.flutter_aliplayer.widget.AliyunVodPlayerView
import com.zxq.flutter_aliplayer.widget.AliyunVodPlayerView.OnPlayerViewClickListener
import com.zxq.flutter_aliplayer.widget.AliyunVodPlayerView.PlayViewType

import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

/**
 * 播放器和播放列表界面 Created by Mulberry on 2018/4/9.
 */
class AliyunPlayerSkinActivity : Activity() {

    private var playerHandler: PlayerHandler? = null
    private var showMoreDialog: AlivcShowMoreDialog? = null

    private val format = SimpleDateFormat("HH:mm:ss.SS")
    private val logStrs = ArrayList<String>()

    private var currentScreenMode = AliyunScreenMode.Small
    private var tvTabDownloadVideo: TextView? = null

    private var mAliyunVodPlayerView: AliyunVodPlayerView? = null

    private var currentError = ErrorInfo.Normal
    //判断是否在后台
    private var mIsInBackground = false
    /**
     * get StsToken stats
     */
    private var inRequest: Boolean = false

    /**
     * 当前tab
     */
    private var currentTab = TAB_VIDEO_LIST
    private var commenUtils: Common? = null
    private var oldTime: Long = 0
    private var downloadOldTime: Long = 0

    private var mCurrentDownloadScreenMode: AliyunScreenMode? = null

    /**
     * 是否需要展示下载界面,如果是恢复数据,则不用展示下载界面
     */
    private var showAddDownloadView: Boolean = false

    /**
     * 是否鉴权过期
     */
    private var mIsTimeExpired = false
    private var downloadDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.NoActionTheme)

        super.onCreate(savedInstanceState)
        showAddDownloadView = false
        setContentView(R.layout.alivc_player_layout_skin)

//        requestVidSts()
        initAliyunPlayerView()
    }

    /**
     * 设置屏幕亮度
     */
    private fun setWindowBrightness(brightness: Int) {
        val window = window
        val lp = window.attributes
        lp.screenBrightness = brightness / 255.0f
        window.attributes = lp
    }

    private fun initAliyunPlayerView() {
        mAliyunVodPlayerView = findViewById<View>(R.id.video_view) as AliyunVodPlayerView
        //保持屏幕敞亮
        mAliyunVodPlayerView!!.keepScreenOn = true
        PlayParameter.PLAY_PARAM_URL = DEFAULT_URL
        val sdDir = Environment.getExternalStorageDirectory().absolutePath + "/test_save_cache"
        mAliyunVodPlayerView!!.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/)
        mAliyunVodPlayerView!!.setTheme(AliyunVodPlayerView.Theme.Green)
        //mAliyunVodPlayerView.setCirclePlay(true);
        mAliyunVodPlayerView!!.setAutoPlay(true)
        val source = UrlSource()
        source.uri = DEFAULT_URL
        mAliyunVodPlayerView!!.setLocalSource(source)

        mAliyunVodPlayerView!!.setOnPreparedListener(MyPrepareListener(this))
        mAliyunVodPlayerView!!.setNetConnectedListener(MyNetConnectedListener(this))
        mAliyunVodPlayerView!!.setOnCompletionListener(MyCompletionListener(this))
        mAliyunVodPlayerView!!.setOnFirstFrameStartListener(MyFrameInfoListener(this))
        mAliyunVodPlayerView!!.setOnChangeQualityListener(MyChangeQualityListener(this))
        //TODO
        mAliyunVodPlayerView!!.setOnStoppedListener(MyStoppedListener(this))
        mAliyunVodPlayerView!!.setmOnPlayerViewClickListener(MyPlayViewClickListener(this))
        mAliyunVodPlayerView!!.setOrientationChangeListener(MyOrientationChangeListener(this))
        //        mAliyunVodPlayerView.setOnUrlTimeExpiredListener(new MyOnUrlTimeExpiredListener(this));
        mAliyunVodPlayerView!!.setOnTimeExpiredErrorListener(MyOnTimeExpiredErrorListener(this))
        mAliyunVodPlayerView!!.setOnShowMoreClickListener(MyShowMoreClickLisener(this))
        mAliyunVodPlayerView!!.setOnPlayStateBtnClickListener(MyPlayStateBtnClickListener(this))
        mAliyunVodPlayerView!!.setOnSeekCompleteListener(MySeekCompleteListener(this))
        mAliyunVodPlayerView!!.setOnSeekStartListener(MySeekStartListener(this))
        mAliyunVodPlayerView!!.setOnScreenBrightness(MyOnScreenBrightnessListener(this))
        mAliyunVodPlayerView!!.setOnErrorListener(MyOnErrorListener(this))
        mAliyunVodPlayerView!!.screenBrightness = BrightnessDialog.getActivityBrightness(this@AliyunPlayerSkinActivity)
        mAliyunVodPlayerView!!.setSeiDataListener(MyOnSeiDataListener(this))
        mAliyunVodPlayerView!!.enableNativeLog()
    }

    /**
     * 请求sts
     */
    private fun requestVidSts() {
        Log.e("scar", "requestVidSts: ")
        if (inRequest) {
            return
        }
        inRequest = true
        if (TextUtils.isEmpty(PlayParameter.PLAY_PARAM_VID)) {
            PlayParameter.PLAY_PARAM_VID = DEFAULT_VID
        }
        Log.e("scar", "requestVidSts:xx ")
        VidStsUtil.getVidSts(PlayParameter.PLAY_PARAM_VID, MyStsListener(this))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //vid/url设置界面并且是取消
        if (requestCode == CODE_REQUEST_SETTING && resultCode == Activity.RESULT_CANCELED) {
            return
        } else if (requestCode == CODE_REQUEST_SETTING && resultCode == Activity.RESULT_OK) {
            setPlaySource()
        }
    }

    /**
     * 播放本地资源
     */
    private fun changePlayLocalSource(url: String, title: String?) {
        val urlSource = UrlSource()
        urlSource.uri = url
        urlSource.title = title
        mAliyunVodPlayerView!!.setLocalSource(urlSource)
    }

    private class MyPrepareListener(skinActivity: AliyunPlayerSkinActivity) : IPlayer.OnPreparedListener {

        private val activityWeakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            activityWeakReference = WeakReference(skinActivity)
        }

        override fun onPrepared() {
            val activity = activityWeakReference.get()
            activity?.onPrepared()
        }
    }

    private fun onPrepared() {

        FixedToastUtils.show(this@AliyunPlayerSkinActivity.applicationContext, R.string.toast_prepare_success)
    }

    private class MyCompletionListener(skinActivity: AliyunPlayerSkinActivity) : IPlayer.OnCompletionListener {

        private val activityWeakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            activityWeakReference = WeakReference(skinActivity)
        }

        override fun onCompletion() {

            val activity = activityWeakReference.get()
            activity?.onCompletion()
        }
    }

    private fun onCompletion() {
        FixedToastUtils.show(this@AliyunPlayerSkinActivity.applicationContext, R.string.toast_play_compleion)
    }

    private class MyFrameInfoListener(skinActivity: AliyunPlayerSkinActivity) : IPlayer.OnRenderingStartListener {

        private val activityWeakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            activityWeakReference = WeakReference(skinActivity)
        }

        override fun onRenderingStart() {
            val activity = activityWeakReference.get()
            activity?.onFirstFrameStart()
        }
    }

    private fun onFirstFrameStart() {

    }

    private inner class MyPlayViewClickListener(activity: AliyunPlayerSkinActivity) : OnPlayerViewClickListener {

        private val weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onClick(screenMode: AliyunScreenMode, viewType: PlayViewType) {
            val currentClickTime = System.currentTimeMillis()
            // 防止快速点击
            if (currentClickTime - oldTime <= 1000) {
                return
            }
            oldTime = currentClickTime
        }
    }

    private class MyChangeQualityListener(skinActivity: AliyunPlayerSkinActivity) : OnChangeQualityListener {

        private val activityWeakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            activityWeakReference = WeakReference(skinActivity)
        }

        override fun onChangeQualitySuccess(finalQuality: String) {

            val activity = activityWeakReference.get()
            activity?.onChangeQualitySuccess(finalQuality)
        }

        override fun onChangeQualityFail(code: Int, msg: String) {
            val activity = activityWeakReference.get()
            activity?.onChangeQualityFail(code, msg)
        }
    }

    private fun onChangeQualitySuccess(finalQuality: String) {
        logStrs.add(format.format(Date()) + getString(R.string.log_change_quality_success))
        FixedToastUtils.show(this@AliyunPlayerSkinActivity.applicationContext,
                getString(R.string.log_change_quality_success))
    }

    internal fun onChangeQualityFail(code: Int, msg: String) {
        logStrs.add(format.format(Date()) + getString(R.string.log_change_quality_fail) + " : " + msg)
        FixedToastUtils.show(this@AliyunPlayerSkinActivity.applicationContext,
                getString(R.string.log_change_quality_fail))
    }

    private class MyStoppedListener(skinActivity: AliyunPlayerSkinActivity) : OnStoppedListener {

        private val activityWeakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            activityWeakReference = WeakReference(skinActivity)
        }

        override fun onStop() {
            val activity = activityWeakReference.get()
            activity?.onStopped()
        }
    }

    private class MyRefreshStsCallback : RefreshStsCallback {

        override fun refreshSts(vid: String, quality: String, format: String, title: String, encript: Boolean): VidSts? {
            VcPlayerLog.d("refreshSts ", "refreshSts , vid = $vid")
            //NOTE: 注意：这个不能启动线程去请求。因为这个方法已经在线程中调用了。
            val vidSts = VidStsUtil.getVidSts(vid)
            if (vidSts == null) {
                return null
            } else {
                vidSts.vid = vid
                vidSts.setQuality(quality, true)
                vidSts.title = title
                return vidSts
            }
        }
    }

    private fun onStopped() {
        FixedToastUtils.show(this@AliyunPlayerSkinActivity.applicationContext, R.string.log_play_stopped)
    }

    private fun setPlaySource() {
        if ("localSource" == PlayParameter.PLAY_PARAM_TYPE) {
            val urlSource = UrlSource()
            urlSource.uri = PlayParameter.PLAY_PARAM_URL
            //默认是5000
            var maxDelayTime = 5000
            if (PlayParameter.PLAY_PARAM_URL.startsWith("artp")) {
                //如果url的开头是artp，将直播延迟设置成100，
                maxDelayTime = 100
            }
            if (mAliyunVodPlayerView != null) {
                val playerConfig = mAliyunVodPlayerView!!.playerConfig
                playerConfig!!.mMaxDelayTime = maxDelayTime
                //开启SEI事件通知
                playerConfig.mEnableSEI = true
                mAliyunVodPlayerView!!.playerConfig = playerConfig
                mAliyunVodPlayerView!!.setLocalSource(urlSource)
            }

        } else if ("vidsts" == PlayParameter.PLAY_PARAM_TYPE) {
            if (!inRequest) {
                val vidSts = VidSts()
                vidSts.vid = PlayParameter.PLAY_PARAM_VID
                vidSts.region = PlayParameter.PLAY_PARAM_REGION
                vidSts.accessKeyId = PlayParameter.PLAY_PARAM_AK_ID
                vidSts.accessKeySecret = PlayParameter.PLAY_PARAM_AK_SECRE
                vidSts.securityToken = PlayParameter.PLAY_PARAM_SCU_TOKEN
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView!!.setVidSts(vidSts)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        mIsInBackground = false
        updatePlayerViewMode()
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView!!.setAutoPlay(true)
            mAliyunVodPlayerView!!.onResume()
        }
    }

    override fun onStop() {
        super.onStop()
        mIsInBackground = true
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView!!.setAutoPlay(false)
            mAliyunVodPlayerView!!.onStop()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updatePlayerViewMode()
    }

    private fun updateDownloadTaskTip() {
        if (currentTab != TAB_DOWNLOAD_LIST) {

            val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.alivc_download_new_task)
            drawable!!.setBounds(0, 0, 20, 20)
            tvTabDownloadVideo!!.compoundDrawablePadding = -20
            tvTabDownloadVideo!!.setCompoundDrawables(null, null, drawable, null)
        } else {
            tvTabDownloadVideo!!.setCompoundDrawables(null, null, null, null)
        }
    }

    private fun updatePlayerViewMode() {
        if (mAliyunVodPlayerView != null) {
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //转为竖屏了。
                //显示状态栏
                //                if (!isStrangePhone()) {
                //                    getSupportActionBar().show();
                //                }

                this.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                mAliyunVodPlayerView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

                //设置view的布局，宽高之类
                val aliVcVideoViewLayoutParams = mAliyunVodPlayerView!!
                        .layoutParams as LinearLayout.LayoutParams
                aliVcVideoViewLayoutParams.height = (ScreenUtils.getWidth(this) * 9.0f / 16).toInt()
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                //转到横屏了。
//                //隐藏状态栏
//                if (!isStrangePhone) {
//                    this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                            WindowManager.LayoutParams.FLAG_FULLSCREEN)
//                    mAliyunVodPlayerView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            or View.SYSTEM_UI_FLAG_FULLSCREEN
//                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//                }
                //设置view的布局，宽高
                val aliVcVideoViewLayoutParams = mAliyunVodPlayerView!!
                        .layoutParams as LinearLayout.LayoutParams
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }

    override fun onDestroy() {
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView!!.onDestroy()
            mAliyunVodPlayerView = null
        }

        if (playerHandler != null) {
            playerHandler!!.removeMessages(DOWNLOAD_ERROR)
            playerHandler = null
        }

        if (commenUtils != null) {
            commenUtils!!.onDestroy()
            commenUtils = null
        }
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (mAliyunVodPlayerView != null) {
            val handler = mAliyunVodPlayerView!!.onKeyDown(keyCode, event)
            if (!handler) {
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //解决某些手机上锁屏之后会出现标题栏的问题。
        updatePlayerViewMode()
    }

    private class PlayerHandler(activity: AliyunPlayerSkinActivity) : Handler() {
        //持有弱引用AliyunPlayerSkinActivity,GC回收时会被回收掉.
        private val mActivty: WeakReference<AliyunPlayerSkinActivity>

        init {
            mActivty = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivty.get()
            super.handleMessage(msg)
            if (activity != null) {
                when (msg.what) {
                    DOWNLOAD_ERROR -> {
//                        ToastUtils.show(activity, msg.data.getString(DOWNLOAD_ERROR_KEY))
                        Log.d("donwload", msg.data.getString(DOWNLOAD_ERROR_KEY))
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private class MyStsListener internal constructor(act: AliyunPlayerSkinActivity) : VidStsUtil.OnStsResultListener {

        private val weakActivity: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakActivity = WeakReference(act)
        }

        override fun onSuccess(vid: String, akid: String, akSecret: String, token: String) {
            val activity = weakActivity.get()
            activity?.onStsSuccess(vid, akid, akSecret, token)
        }

        override fun onFail() {
            val activity = weakActivity.get()
            activity?.onStsFail()
        }
    }

    private fun onStsFail() {

        FixedToastUtils.show(applicationContext, R.string.request_vidsts_fail)
        inRequest = false
        //finish();
    }

    private fun onStsSuccess(mVid: String, akid: String, akSecret: String, token: String) {
        PlayParameter.PLAY_PARAM_VID = mVid
        PlayParameter.PLAY_PARAM_AK_ID = akid
        PlayParameter.PLAY_PARAM_AK_SECRE = akSecret
        PlayParameter.PLAY_PARAM_SCU_TOKEN = token

        mIsTimeExpired = false

        inRequest = false

    }

    private class MyOrientationChangeListener(activity: AliyunPlayerSkinActivity) : AliyunVodPlayerView.OnOrientationChangeListener {

        private val weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun orientationChange(from: Boolean, currentMode: AliyunScreenMode) {
            val activity = weakReference.get()

            if (activity != null) {
                activity.hideDownloadDialog(from, currentMode)
                activity.hideShowMoreDialog(from, currentMode)

            }
        }
    }

    private fun hideShowMoreDialog(from: Boolean, currentMode: AliyunScreenMode) {
        if (showMoreDialog != null) {
            if (currentMode == AliyunScreenMode.Small) {
                showMoreDialog!!.dismiss()
                currentScreenMode = currentMode
            }
        }
    }

    private fun hideDownloadDialog(from: Boolean, currentMode: AliyunScreenMode) {

        if (downloadDialog != null) {
            if (currentScreenMode != currentMode) {
                downloadDialog!!.dismiss()
                currentScreenMode = currentMode
            }
        }
    }

    /**
     * 判断是否有网络的监听
     */
    private inner class MyNetConnectedListener(activity: AliyunPlayerSkinActivity) : AliyunVodPlayerView.NetConnectedListener {
        internal var weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onReNetConnected(isReconnect: Boolean) {
            val activity = weakReference.get()
            activity?.onReNetConnected(isReconnect)
        }

        override fun onNetUnConnected() {
            val activity = weakReference.get()
            activity?.onNetUnConnected()
        }
    }

    private fun onNetUnConnected() {
        currentError = ErrorInfo.UnConnectInternet
    }

    private fun onReNetConnected(isReconnect: Boolean) {
        currentError = ErrorInfo.Normal
    }

    /**
     * 因为鉴权过期,而去重新鉴权
     */
    private class RetryExpiredSts(activity: AliyunPlayerSkinActivity) : VidStsUtil.OnStsResultListener {

        private val weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onSuccess(vid: String, akid: String, akSecret: String, token: String) {
            val activity = weakReference.get()
            activity?.onStsRetrySuccess(vid, akid, akSecret, token)
        }

        override fun onFail() {

        }
    }

    private fun onStsRetrySuccess(mVid: String, akid: String, akSecret: String, token: String) {
        PlayParameter.PLAY_PARAM_VID = mVid
        PlayParameter.PLAY_PARAM_AK_ID = akid
        PlayParameter.PLAY_PARAM_AK_SECRE = akSecret
        PlayParameter.PLAY_PARAM_SCU_TOKEN = token

        inRequest = false
        mIsTimeExpired = false

        val vidSts = VidSts()
        vidSts.vid = PlayParameter.PLAY_PARAM_VID
        vidSts.region = PlayParameter.PLAY_PARAM_REGION
        vidSts.accessKeyId = PlayParameter.PLAY_PARAM_AK_ID
        vidSts.accessKeySecret = PlayParameter.PLAY_PARAM_AK_SECRE
        vidSts.securityToken = PlayParameter.PLAY_PARAM_SCU_TOKEN

        mAliyunVodPlayerView!!.setVidSts(vidSts)
    }

    class MyOnTimeExpiredErrorListener(activity: AliyunPlayerSkinActivity) : AliyunVodPlayerView.OnTimeExpiredErrorListener {

        internal var weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onTimeExpiredError() {
            val activity = weakReference.get()
            activity?.onTimExpiredError()
        }
    }

    private fun onUrlTimeExpired(oldVid: String, oldQuality: String) {
        //requestVidSts();
        val vidSts = VidStsUtil.getVidSts(oldVid)
        PlayParameter.PLAY_PARAM_VID = vidSts!!.vid
        PlayParameter.PLAY_PARAM_AK_SECRE = vidSts.accessKeySecret
        PlayParameter.PLAY_PARAM_AK_ID = vidSts.accessKeyId
        PlayParameter.PLAY_PARAM_SCU_TOKEN = vidSts.securityToken

        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView!!.setVidSts(vidSts)
        }
    }

    /**
     * 鉴权过期
     */
    private fun onTimExpiredError() {
        VidStsUtil.getVidSts(PlayParameter.PLAY_PARAM_VID, RetryExpiredSts(this))
    }

    private class MyShowMoreClickLisener internal constructor(activity: AliyunPlayerSkinActivity) : ControlView.OnShowMoreClickListener {
        internal var weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun showMore() {
            val activity = weakReference.get()
            if (activity != null) {
                val currentClickTime = System.currentTimeMillis()
                // 防止快速点击
                if (currentClickTime - activity.oldTime <= 1000) {
                    return
                }
                activity.oldTime = currentClickTime
//                activity.showMore(activity)
//                activity.requestVidSts()
            }

        }
    }

    private fun showMore(activity: AliyunPlayerSkinActivity) {
        showMoreDialog = AlivcShowMoreDialog(activity)
        val moreValue = AliyunShowMoreValue()
        moreValue.speed = mAliyunVodPlayerView!!.currentSpeed
        moreValue.volume = mAliyunVodPlayerView!!.currentVolume.toInt()

        val showMoreView = ShowMoreView(activity, moreValue)
        showMoreDialog!!.setContentView(showMoreView)
        showMoreDialog!!.show()
        showMoreView.setOnDownloadButtonClickListener(object : ShowMoreView.OnDownloadButtonClickListener {
            override fun onDownloadClick() {
                val currentClickTime = System.currentTimeMillis()
                // 防止快速点击
                if (currentClickTime - downloadOldTime <= 1000) {
                    return
                }
                downloadOldTime = currentClickTime
                // 点击下载
                showMoreDialog!!.dismiss()
                if ("url" == PlayParameter.PLAY_PARAM_TYPE || "localSource" == PlayParameter.PLAY_PARAM_TYPE) {
                    FixedToastUtils.show(activity, resources.getString(R.string.alivc_video_not_support_download))
                    return
                }
                mCurrentDownloadScreenMode = AliyunScreenMode.Full
                showAddDownloadView = true
                if (mAliyunVodPlayerView != null) {
                    val currentMediaInfo = mAliyunVodPlayerView!!.currentMediaInfo
                }
            }
        })

        showMoreView.setOnScreenCastButtonClickListener(object : ShowMoreView.OnScreenCastButtonClickListener {
            override fun onScreenCastClick() {
                Toast.makeText(activity, "功能正在开发中......", Toast.LENGTH_SHORT).show()
                //                TODO 2019年04月18日16:43:29  先屏蔽投屏功能
                //                showMoreDialog.dismiss();
                //                showScreenCastView();
            }
        })

        showMoreView.setOnBarrageButtonClickListener(object : ShowMoreView.OnBarrageButtonClickListener {
            override fun onBarrageClick() {
                Toast.makeText(activity, "功能正在开发中......", Toast.LENGTH_SHORT).show()
                //                if (showMoreDialog != null && showMoreDialog.isShowing()) {
                //                    showMoreDialog.dismiss();
                //                }
            }
        })

        showMoreView.setOnSpeedCheckedChangedListener(object : ShowMoreView.OnSpeedCheckedChangedListener {
            override fun onSpeedChanged(group: RadioGroup, checkedId: Int) {
                // 点击速度切换
                if (checkedId == R.id.rb_speed_normal) {
                    mAliyunVodPlayerView!!.changeSpeed(SpeedValue.One)
                } else if (checkedId == R.id.rb_speed_onequartern) {
                    mAliyunVodPlayerView!!.changeSpeed(SpeedValue.OneQuartern)
                } else if (checkedId == R.id.rb_speed_onehalf) {
                    mAliyunVodPlayerView!!.changeSpeed(SpeedValue.OneHalf)
                } else if (checkedId == R.id.rb_speed_twice) {
                    mAliyunVodPlayerView!!.changeSpeed(SpeedValue.Twice)
                }

            }
        })

        /**
         * 初始化亮度
         */
        if (mAliyunVodPlayerView != null) {
            showMoreView.setBrightness(mAliyunVodPlayerView!!.screenBrightness)
        }
        // 亮度seek
        showMoreView.setOnLightSeekChangeListener(object : ShowMoreView.OnLightSeekChangeListener {
            override fun onStart(seekBar: SeekBar) {

            }

            override fun onProgress(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                setWindowBrightness(progress)
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView!!.screenBrightness = progress
                }
            }

            override fun onStop(seekBar: SeekBar) {

            }
        })

        /**
         * 初始化音量
         */
        if (mAliyunVodPlayerView != null) {
            showMoreView.setVoiceVolume(mAliyunVodPlayerView!!.currentVolume)
        }
        showMoreView.setOnVoiceSeekChangeListener(object : ShowMoreView.OnVoiceSeekChangeListener {
            override fun onStart(seekBar: SeekBar) {

            }

            override fun onProgress(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mAliyunVodPlayerView!!.currentVolume = progress / 100.00f
            }

            override fun onStop(seekBar: SeekBar) {

            }
        })

    }

    /**
     * 获取url的scheme
     *
     * @param url
     * @return
     */
    private fun getUrlScheme(url: String): String? {
        return Uri.parse(url).scheme
    }

    private class MyPlayStateBtnClickListener internal constructor(activity: AliyunPlayerSkinActivity) : AliyunVodPlayerView.OnPlayStateBtnClickListener {
        internal var weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onPlayBtnClick(playerState: Int) {
            val activity = weakReference.get()
            activity?.onPlayStateSwitch(playerState)
        }
    }

    /**
     * 播放状态切换
     */
    private fun onPlayStateSwitch(playerState: Int) {

    }

    private class MySeekCompleteListener internal constructor(activity: AliyunPlayerSkinActivity) : IPlayer.OnSeekCompleteListener {
        internal var weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onSeekComplete() {
            val activity = weakReference.get()
            activity?.onSeekComplete()
        }
    }

    private fun onSeekComplete() {
    }

    private class MySeekStartListener internal constructor(activity: AliyunPlayerSkinActivity) : AliyunVodPlayerView.OnSeekStartListener {
        internal var weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onSeekStart(position: Int) {
            val activity = weakReference.get()
            activity?.onSeekStart(position)
        }
    }

    private class MyOnFinishListener(activity: AliyunPlayerSkinActivity) : AliyunVodPlayerView.OnFinishListener {

        internal var weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onFinishClick() {
            val aliyunPlayerSkinActivity = weakReference.get()
            aliyunPlayerSkinActivity?.finish()
        }
    }

    private class MyOnScreenBrightnessListener(activity: AliyunPlayerSkinActivity) : AliyunVodPlayerView.OnScreenBrightnessListener {

        private val weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onScreenBrightness(brightness: Int) {
            val aliyunPlayerSkinActivity = weakReference.get()
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.setWindowBrightness(brightness)
                if (aliyunPlayerSkinActivity.mAliyunVodPlayerView != null) {
                    aliyunPlayerSkinActivity.mAliyunVodPlayerView!!.screenBrightness = brightness
                }
            }
        }
    }

    /**
     * 播放器出错监听
     */
    private class MyOnErrorListener(activity: AliyunPlayerSkinActivity) : IPlayer.OnErrorListener {

        private val weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onError(errorInfo: com.aliyun.player.bean.ErrorInfo) {
            val aliyunPlayerSkinActivity = weakReference.get()
            aliyunPlayerSkinActivity?.onError(errorInfo)
        }
    }

    private class MyOnSeiDataListener(activity: AliyunPlayerSkinActivity) : IPlayer.OnSeiDataListener {
        private val weakReference: WeakReference<AliyunPlayerSkinActivity>

        init {
            weakReference = WeakReference(activity)
        }

        override fun onSeiData(i: Int, bytes: ByteArray) {
        }
    }

    private fun onError(errorInfo: com.aliyun.player.bean.ErrorInfo) {
        //鉴权过期
        if (errorInfo.code.value == ErrorCode.ERROR_SERVER_POP_UNKNOWN.value) {
            mIsTimeExpired = true
        }
    }

    private fun onSeekStart(position: Int) {
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event != null && event.keyCode == 67) {
            if (mAliyunVodPlayerView != null) {
                //删除按键监听,部分手机在EditText没有内容时,点击删除按钮会隐藏软键盘
                return false
            }
        }
        return super.dispatchKeyEvent(event)
    }

    companion object {
        /**
         * 开启设置界面的请求码
         */
        private val CODE_REQUEST_SETTING = 1000
        /**
         * 设置界面返回的结果码, 100为vid类型, 200为url类型
         */
        private val CODE_RESULT_TYPE_VID = 100
        private val CODE_RESULT_TYPE_URL = 200
        private val DEFAULT_URL = "http://player.alicdn.com/video/aliyunmedia.mp4"
        private val DEFAULT_VID = "8bb9b7d5c7c64cf49d51fa808b1f0957"

        private val PERMISSIONS_STORAGE = arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")

        private val REQUEST_EXTERNAL_STORAGE = 1
        private val TAB_VIDEO_LIST = 1
        private val TAB_LOG = 2
        private val TAB_DOWNLOAD_LIST = 3
        private var preparedVid: String? = null

        private val DOWNLOAD_ERROR = 1
        private val DOWNLOAD_ERROR_KEY = "error_key"
    }
}
