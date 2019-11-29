package com.zxq.flutter_aliplayer.utils

import android.os.AsyncTask

import com.aliyun.player.source.VidSts

/**
 * Created by pengshuang on 31/08/2017.
 */
object VidStsUtil {


    private val TAG = VidStsUtil::class.java.simpleName

    fun getVidSts(videoId: String): VidSts? {
        return null
//        try {
//            //以前的连接地址"https://demo-vod.cn-shanghai.aliyuncs.com/voddemo/CreateSecurityToken?BusinessType=vodai&TerminalType=pc&DeviceModel=iPhone9,2&UUID=59ECA-4193-4695-94DD-7E1247288&AppVersion=1.0.0&VideoId=" + videoId"
//            val stsUrl = AliyunVodHttpCommon.getInstance()!!.vodStsDomain + "getSts"
//            val response = HttpClientUtil.doGet(stsUrl)
//            val jsonObject = JSONObject(response)
//
//            val securityTokenInfo = jsonObject.getJSONObject("data")
//            if (securityTokenInfo == null) {
//
//                VcPlayerLog.e(TAG, "SecurityTokenInfo == null ")
//                return null
//            }
//
//            val accessKeyId = securityTokenInfo.getString("accessKeyId")
//            val accessKeySecret = securityTokenInfo.getString("accessKeySecret")
//            val securityToken = securityTokenInfo.getString("securityToken")
//            val expiration = securityTokenInfo.getString("expiration")
//
//            VcPlayerLog.e("radish", "accessKeyId = " + accessKeyId + " , accessKeySecret = " + accessKeySecret +
//                    " , securityToken = " + securityToken + " ,expiration = " + expiration)
//
//            val vidSts = VidSts()
//            vidSts.vid = videoId
//            vidSts.accessKeyId = accessKeyId
//            vidSts.accessKeySecret = accessKeySecret
//            vidSts.securityToken = securityToken
//            return vidSts
//
//        } catch (e: Exception) {
//            VcPlayerLog.e(TAG, "e = " + e.message)
//            return null
//        }

    }

    interface OnStsResultListener {
        fun onSuccess(vid: String, akid: String, akSecret: String, token: String)

        fun onFail()
    }

    fun getVidSts(vid: String, onStsResultListener: OnStsResultListener) {
        val asyncTask = object : AsyncTask<Void, Void, VidSts>() {

            override fun doInBackground(vararg params: Void): VidSts? {
                return getVidSts(vid)
            }

            override fun onPostExecute(s: VidSts?) {
                if (s == null) {
                    onStsResultListener.onFail()
                } else {
                    onStsResultListener.onSuccess(s.vid, s.accessKeyId, s.accessKeySecret, s.securityToken)
                }
            }
        }
        asyncTask.execute()

        return
    }


}
