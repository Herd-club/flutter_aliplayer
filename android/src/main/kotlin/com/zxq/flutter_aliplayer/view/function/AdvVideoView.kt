package com.zxq.flutter_aliplayer.view.function

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.RelativeLayout

import com.aliyun.player.AliPlayer
import com.aliyun.player.AliPlayerFactory
import com.aliyun.player.IPlayer
import com.aliyun.player.bean.ErrorInfo
import com.aliyun.player.bean.InfoBean
import com.aliyun.player.source.UrlSource
import com.aliyun.player.source.VidAuth
import com.aliyun.player.source.VidMps
import com.aliyun.player.source.VidSts
import com.aliyun.utils.VcPlayerLog

/**
 * 视频广告View
 * @author hanyu
 */
class AdvVideoView : RelativeLayout {

    //广告播放器的surfaceView
    var advSurfaceView: SurfaceView? = null
        private set
    //用于播放视频广告的播放器
    //获取视频广告的播放器
    var advVideoAliyunVodPlayer: AliPlayer? = null
        private set

    //对外info改变监听
    private var mOutOnInfoListener: IPlayer.OnInfoListener? = null
    //对外错误监听
    private var mOutOnErrorListener: IPlayer.OnErrorListener? = null
    //对外播放完成监听
    private var mOutOnCompletionListener: IPlayer.OnCompletionListener? = null
    //对外loading状态监听
    private var mOutOnLoadingStatusListener: IPlayer.OnLoadingStatusListener? = null
    //对外renderingStart监听
    private var mOutOnRenderingStartListener: IPlayer.OnRenderingStartListener? = null
    //状态改变监听
    private var mOutOnStateChangedListener: IPlayer.OnStateChangedListener? = null
    //对外准备完成监听
    private var mOutPreparedListener: IPlayer.OnPreparedListener? = null
    //播放器的状态
    /**
     * 获取视频广告播放器的状态
     */
    var advPlayerState = -1
        private set


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        initSurfaceView()
        initAdvPlayer()
    }

    private fun initSurfaceView() {
        advSurfaceView = SurfaceView(context.applicationContext)
        addSubView(advSurfaceView!!)
    }

    /**
     * addSubView 添加子view到布局中
     *
     * @param view 子view
     */
    private fun addSubView(view: View) {
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        //添加到布局中
        addView(view, params)
    }

    fun setSurfaceViewVisibility(visibility: Int) {
        advSurfaceView!!.visibility = visibility
    }

    /**
     * 初始化视频广告
     */
    private fun initAdvPlayer() {
        val holder = advSurfaceView!!.holder
        //增加surfaceView的监听
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                VcPlayerLog.d(TAG, " surfaceCreated = surfaceHolder = $surfaceHolder")
                if (advVideoAliyunVodPlayer != null) {
                    advVideoAliyunVodPlayer!!.setDisplay(surfaceHolder)
                    //防止黑屏
                    advVideoAliyunVodPlayer!!.redraw()
                }

            }

            override fun surfaceChanged(surfaceHolder: SurfaceHolder, format: Int, width: Int,
                                        height: Int) {
                VcPlayerLog.d(TAG,
                        " surfaceChanged surfaceHolder = $surfaceHolder ,  width = $width , height = $height")
                if (advVideoAliyunVodPlayer != null) {
                    advVideoAliyunVodPlayer!!.redraw()
                }
            }

            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
                VcPlayerLog.d(TAG, " surfaceDestroyed = surfaceHolder = $surfaceHolder")
                if (advVideoAliyunVodPlayer != null) {
                    advVideoAliyunVodPlayer!!.setDisplay(null)
                }
            }
        })

        //该播放器用于播放器视频广告
        advVideoAliyunVodPlayer = AliPlayerFactory.createAliPlayer(context)
        advVideoAliyunVodPlayer!!.isAutoPlay = true
        //设置准备回调
        advVideoAliyunVodPlayer!!.setOnPreparedListener {
            if (mOutPreparedListener != null) {
                mOutPreparedListener!!.onPrepared()
            }
        }

        //播放器出错监听
        advVideoAliyunVodPlayer!!.setOnErrorListener { errorInfo ->
            if (mOutOnErrorListener != null) {
                mOutOnErrorListener!!.onError(errorInfo)
            }
        }

        //播放器加载回调
        advVideoAliyunVodPlayer!!.setOnLoadingStatusListener(object : IPlayer.OnLoadingStatusListener {
            override fun onLoadingBegin() {
                if (mOutOnLoadingStatusListener != null) {
                    mOutOnLoadingStatusListener!!.onLoadingBegin()
                }
            }

            override fun onLoadingProgress(percent: Int, speed: Float) {
                if (mOutOnLoadingStatusListener != null) {
                    mOutOnLoadingStatusListener!!.onLoadingProgress(percent, speed)
                }
            }

            override fun onLoadingEnd() {
                if (mOutOnLoadingStatusListener != null) {
                    mOutOnLoadingStatusListener!!.onLoadingEnd()
                }
            }
        })

        //播放结束
        advVideoAliyunVodPlayer!!.setOnCompletionListener {
            if (mOutOnCompletionListener != null) {
                mOutOnCompletionListener!!.onCompletion()
            }
        }

        //播放器状态监听
        //        mAdvVideoAliyunVodPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
        //            @Override
        //            public void onStateChanged(int newState) {
        //                //暂停状态
        //                if (newState == IPlayer.stopped) {
        //                    if (mOnStoppedListener != null) {
        //                        mOnStoppedListener.onStop();
        //                    }
        //                }
        //            }
        //        });

        //播放信息监听
        advVideoAliyunVodPlayer!!.setOnInfoListener { infoBean ->
            if (mOutOnInfoListener != null) {
                mOutOnInfoListener!!.onInfo(infoBean)
            }
        }

        //第一帧显示
        advVideoAliyunVodPlayer!!.setOnRenderingStartListener {
            if (mOutOnRenderingStartListener != null) {
                mOutOnRenderingStartListener!!.onRenderingStart()
            }
        }

        //状态改变监听
        advVideoAliyunVodPlayer!!.setOnStateChangedListener { newState ->
            advPlayerState = newState
            if (mOutOnStateChangedListener != null) {
                mOutOnStateChangedListener!!.onStateChanged(newState)
            }
        }

        //url过期监听
        //        mAdvVideoAliyunVodPlayer.setOnUrlTimeExpiredListener(new IAliyunVodPlayer.OnUrlTimeExpiredListener() {
        //            @Override
        //            public void onUrlTimeExpired(String vid, String quality) {
        //                if (mOutUrlTimeExpiredListener != null) {
        //                    mOutUrlTimeExpiredListener.onUrlTimeExpired(vid, quality);
        //                }
        //            }
        //        });
        //
        //        //请求源过期信息
        //        mAdvVideoAliyunVodPlayer.setOnTimeExpiredErrorListener(new IAliyunVodPlayer.OnTimeExpiredErrorListener() {
        //            @Override
        //            public void onTimeExpiredError() {
        //                if (mTipsView != null) {
        //                    mTipsView.hideAll();
        //                    mTipsView.showErrorTipViewWithoutCode("鉴权过期");
        //                    mTipsView.setOnTipClickListener(new TipsView.OnTipClickListener() {
        //                        @Override
        //                        public void onContinuePlay() {
        //                        }
        //
        //                        @Override
        //                        public void onStopPlay() {
        //                        }
        //
        //                        @Override
        //                        public void onRetryPlay() {
        //                            if (mOutTimeExpiredErrorListener != null) {
        //                                mOutTimeExpiredErrorListener.onTimeExpiredError();
        //                            }
        //                        }
        //
        //                        @Override
        //                        public void onReplay() {
        //                        }
        //                    });
        //                }
        //            }
        //        });

        advVideoAliyunVodPlayer!!.setDisplay(advSurfaceView!!.holder)
    }

    /**
     * 设置vidSts
     */
    fun optionSetVidSts(vidSts: VidSts) {
        if (advVideoAliyunVodPlayer != null) {
            advVideoAliyunVodPlayer!!.setDataSource(vidSts)
        }
    }

    /**
     * 设置vidSuth
     */
    fun optionSetVidAuth(vidAuth: VidAuth) {
        if (advVideoAliyunVodPlayer != null) {
            advVideoAliyunVodPlayer!!.setDataSource(vidAuth)
        }
    }

    /**
     * 设置urlSource
     */
    fun optionSetUrlSource(urlSource: UrlSource) {
        if (advVideoAliyunVodPlayer != null) {
            advVideoAliyunVodPlayer!!.setDataSource(urlSource)
        }
    }

    /**
     * 设置vidMps
     */
    fun optionSetVidMps(vidMps: VidMps) {
        if (advVideoAliyunVodPlayer != null) {
            advVideoAliyunVodPlayer!!.setDataSource(vidMps)
        }
    }

    /**
     * prepared操作
     */
    fun optionPrepare() {
        if (advVideoAliyunVodPlayer != null) {
            advVideoAliyunVodPlayer!!.prepare()
        }
    }

    /**
     * 开始操作
     */
    fun optionStart() {
        if (advVideoAliyunVodPlayer != null) {
            advVideoAliyunVodPlayer!!.start()
        }
    }

    /**
     * 暂停操作
     */
    fun optionPause() {
        if (advVideoAliyunVodPlayer != null) {
            advVideoAliyunVodPlayer!!.pause()
        }
    }

    /**
     * 停止操作
     */
    fun optionStop() {
        if (advVideoAliyunVodPlayer != null) {
            advVideoAliyunVodPlayer!!.stop()
        }
    }

    /**
     * 设置prepared监听
     */
    fun setOutPreparedListener(outPreparedListener: IPlayer.OnPreparedListener) {
        this.mOutPreparedListener = outPreparedListener
    }

    /**
     * 设置onLoading状态监听
     */
    fun setOutOnLoadingStatusListener(onLoadingStatusListener: IPlayer.OnLoadingStatusListener) {
        this.mOutOnLoadingStatusListener = onLoadingStatusListener
    }

    /**
     * 设置播放完成监听
     */
    fun setOutOnCompletionListener(onCompletionListener: IPlayer.OnCompletionListener) {
        this.mOutOnCompletionListener = onCompletionListener
    }

    /**
     * 设置对外info改变监听
     */
    fun setOutOnInfoListener(onInfoListener: IPlayer.OnInfoListener) {
        this.mOutOnInfoListener = onInfoListener
    }

    /**
     * 设置对外 renderingStart 监听
     */
    fun setOutOnRenderingStartListener(onRenderingStartListener: IPlayer.OnRenderingStartListener) {
        this.mOutOnRenderingStartListener = onRenderingStartListener
    }

    /**
     * 设置对外错误监听
     */
    fun setOutOnErrorListener(onErrorListener: IPlayer.OnErrorListener) {
        this.mOutOnErrorListener = onErrorListener
    }

    //状态改变监听
    fun setOutOnStateChangedListener(listener: IPlayer.OnStateChangedListener) {
        this.mOutOnStateChangedListener = listener
    }

    fun setAutoPlay(autoPlay: Boolean) {
        if (advVideoAliyunVodPlayer != null) {
            advVideoAliyunVodPlayer!!.isAutoPlay = autoPlay
        }
    }

    enum class VideoState {
        /**
         * 广告
         */
        VIDEO_ADV,
        /**
         * 原视频
         */
        VIDEO_SOURCE
    }

    /**
     * 将要播放的视频
     */
    enum class IntentPlayVideo {
        /**
         * 先播放中间广告,播放完成后再播放最后一条广告
         */
        MIDDLE_END_ADV_SEEK,
        /**
         * 播放中间广告,并且播放完成需要seek
         */
        MIDDLE_ADV_SEEK,
        /**
         * 播放开始广告
         */
        START_ADV,
        /**
         * 播放中间广告
         */
        MIDDLE_ADV,
        /**
         * 播放结束广告
         */
        END_ADV,
        /**
         * 原视频左seek
         */
        REVERSE_SOURCE,
        /**
         * 正常seek
         */
        NORMAL
    }

    companion object {

        private val TAG = AdvVideoView::class.java.simpleName
    }
}
