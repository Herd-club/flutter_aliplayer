package com.zxq.flutter_aliplayer.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import com.zxq.flutter_aliplayer.R
import com.zxq.flutter_aliplayer.constants.PlayParameter
import com.zxq.flutter_aliplayer.listener.OnNotifyActivityListener
import com.zxq.flutter_aliplayer.utils.FixedToastUtils
import com.zxq.flutter_aliplayer.utils.VidStsUtil

import java.lang.ref.WeakReference

/**
 * vid设置界面
 * Created by Mulberry on 2018/4/4.
 */
class AliyunVidPlayFragment : Fragment() {

    internal lateinit var etVid: EditText
    internal lateinit var etAkId: EditText
    internal lateinit var etAkSecret: EditText
    internal lateinit var etScuToken: EditText
    /**
     * get StsToken stats
     */
    private var inRequest: Boolean = false

    private var onNotifyActivityListener: OnNotifyActivityListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_player_vidplay_layout, container, false)

        initStsView(v)
        return v
    }

    private fun initStsView(v: View) {
        etVid = v.findViewById<View>(R.id.vid) as EditText
        etAkId = v.findViewById<View>(R.id.akId) as EditText
        etAkSecret = v.findViewById<View>(R.id.akSecret) as EditText
        etScuToken = v.findViewById<View>(R.id.scuToken) as EditText
    }

    fun startToPlayerByVid() {
        val mVid = etVid.text.toString()
        val akId = etAkId.text.toString()
        val akSecret = etAkSecret.text.toString()
        val scuToken = etScuToken.text.toString()
        PlayParameter.PLAY_PARAM_TYPE = "vidsts"
        if (TextUtils.isEmpty(mVid) || TextUtils.isEmpty(akId) || TextUtils.isEmpty(akSecret) || TextUtils.isEmpty(scuToken)) {
            if (inRequest) {
                return
            }

            inRequest = true
            VidStsUtil.getVidSts(mVid, MyStsListener(this))

        } else {

            PlayParameter.PLAY_PARAM_VID = mVid
            PlayParameter.PLAY_PARAM_AK_ID = akId
            PlayParameter.PLAY_PARAM_AK_SECRE = akSecret
            PlayParameter.PLAY_PARAM_SCU_TOKEN = scuToken

            activity!!.setResult(CODE_RESULT_VID)
            activity!!.finish()
        }
    }

    private class MyStsListener(view: AliyunVidPlayFragment) : VidStsUtil.OnStsResultListener {

        private val weakctivity: WeakReference<AliyunVidPlayFragment>

        init {
            weakctivity = WeakReference(view)
        }

        override fun onSuccess(vid: String, akid: String, akSecret: String, token: String) {
            val fragment = weakctivity.get()
            fragment?.onStsSuccess(vid, akid, akSecret, token)
        }

        override fun onFail() {
            val fragment = weakctivity.get()
            fragment?.onStsFail()
        }
    }

    private fun onStsFail() {
        if (context != null) {
            FixedToastUtils.show(context!!.applicationContext, R.string.request_vidsts_fail)
        }
        inRequest = false
    }

    private fun onStsSuccess(mVid: String, akid: String, akSecret: String, token: String) {

        PlayParameter.PLAY_PARAM_VID = mVid
        PlayParameter.PLAY_PARAM_AK_ID = akid
        PlayParameter.PLAY_PARAM_AK_SECRE = akSecret
        PlayParameter.PLAY_PARAM_SCU_TOKEN = token

        if (onNotifyActivityListener != null) {
            onNotifyActivityListener!!.onNotifyActivity()
        }

        //getActivity().setResult(CODE_RESULT_VID);
        //getActivity().finish();
        //

        inRequest = false
    }

    fun setOnNotifyActivityListener(listener: OnNotifyActivityListener) {
        this.onNotifyActivityListener = listener
    }

    companion object {

        /**
         * 返回给上个activity的resultcode: 100为vid播放类型, 200为URL播放类型
         */
        private val CODE_RESULT_VID = 100
    }
}
