package com.zxq.flutter_aliplayer.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView

import com.zxq.flutter_aliplayer.R
import com.zxq.flutter_aliplayer.constants.PlayParameter
import com.zxq.flutter_aliplayer.listener.OnNotifyActivityListener
import com.zxq.flutter_aliplayer.utils.FixedToastUtils


/**
 * url设置界面
 * Created by Mulberry on 2018/4/4.
 */
class AliyunUrlPlayFragment : Fragment(), OnClickListener {
    override fun onClick(p0: View?) {

    }

    internal lateinit var ivQRcode: ImageView
    internal lateinit var etPlayUrl: EditText

    private var onNotifyActivityListener: OnNotifyActivityListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_player_urlplay_layout, container, false)

        ivQRcode = v.findViewById<View>(R.id.iv_qrcode) as ImageView
        etPlayUrl = v.findViewById<View>(R.id.et_play_url) as EditText
        ivQRcode.setOnClickListener(this)
        return v
    }

    /**
     * start player by Url
     */
    fun startPlayerByUrl() {
        if (!TextUtils.isEmpty(etPlayUrl.text.toString())) {
            val intent = Intent()
            intent.setClass(this.activity!!, AliyunPlayerSkinActivity::class.java)

            PlayParameter.PLAY_PARAM_TYPE = "localSource"
            PlayParameter.PLAY_PARAM_URL = etPlayUrl.text.toString()

            //getActivity().setResult(CODE_RESULT_URL);
            //getActivity().finish();
            if (onNotifyActivityListener != null) {
                onNotifyActivityListener!!.onNotifyActivity()
            }
        } else {
            FixedToastUtils.show(this.activity!!.applicationContext, R.string.play_url_null_toast)
        }

    }

    fun setOnNotifyActivityListener(listener: OnNotifyActivityListener) {
        this.onNotifyActivityListener = listener
    }

    companion object {
        private val REQ_CODE_PERMISSION = 0x1111

        /**
         * 返回给上个activity的resultcode: 100为vid播放类型, 200为URL播放类型
         */
        private val CODE_RESULT_URL = 200
    }
}
