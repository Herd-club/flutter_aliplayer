package com.zxq.flutter_aliplayer.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.zxq.flutter_aliplayer.R
import com.zxq.flutter_aliplayer.listener.OnNotifyActivityListener

import java.util.ArrayList

/**
 * 设置界面, 主要是UrlPlayFragment和VidPlayFragment的寄生Activity
 * Created by Mulberry on 2018/4/4.
 */
class AliyunPlayerSettingActivity : FragmentActivity(), OnClickListener, OnNotifyActivityListener {

    private var tvVidplay: TextView? = null
    private var tvUrlplay: TextView? = null
    private var ivVidplay: ImageView? = null
    private var ivUrlplay: ImageView? = null
    private var btnStartPlayer: Button? = null

    private var fragmentArrayList: ArrayList<Fragment>? = null
    internal lateinit var aliyunVidPlayFragment: AliyunVidPlayFragment
    internal lateinit var aliyunUrlPlayFragment: AliyunUrlPlayFragment
    private var ivBack: ImageView? = null

    private var mCurrentFrgment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_setting_layout)
        tvVidplay = findViewById<View>(R.id.tv_vidplay) as TextView
        tvUrlplay = findViewById<View>(R.id.tv_urlplay) as TextView
        ivVidplay = findViewById<View>(R.id.iv_vidplay) as ImageView
        ivUrlplay = findViewById<View>(R.id.iv_urlplay) as ImageView
        ivBack = findViewById<View>(R.id.iv_back) as ImageView
        btnStartPlayer = findViewById<View>(R.id.btn_start_player) as Button

        fragmentArrayList = ArrayList()
        aliyunVidPlayFragment = AliyunVidPlayFragment()
        aliyunUrlPlayFragment = AliyunUrlPlayFragment()
        aliyunVidPlayFragment.setOnNotifyActivityListener(this)
        aliyunUrlPlayFragment.setOnNotifyActivityListener(this)
        fragmentArrayList!!.add(aliyunVidPlayFragment)
        fragmentArrayList!!.add(aliyunUrlPlayFragment)

        tvVidplay!!.setOnClickListener(this)
        tvUrlplay!!.setOnClickListener(this)
        ivBack!!.setOnClickListener(this)
        btnStartPlayer!!.setOnClickListener(this)

        ivVidplay!!.isActivated = true
        ivUrlplay!!.isActivated = false

        changeFragment(FRAGMENT_VID_PLAY)
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.tv_vidplay) {

            changeFragment(FRAGMENT_VID_PLAY)
            ivVidplay!!.isActivated = true
            ivUrlplay!!.isActivated = false

        } else if (i == R.id.tv_urlplay) {

            changeFragment(FRAGMENT_URL_PLAY)
            ivUrlplay!!.isActivated = true
            ivVidplay!!.isActivated = false

        } else if (i == R.id.btn_start_player) {
            if (mCurrentFrgment is AliyunVidPlayFragment) {
                aliyunVidPlayFragment.startToPlayerByVid()
            } else if (mCurrentFrgment is AliyunUrlPlayFragment) {
                aliyunUrlPlayFragment.startPlayerByUrl()
            }
        } else if (i == R.id.iv_back) {
            finish()
        }
    }

    /**
     * use index to change fragment
     *
     * @param index
     */
    private fun changeFragment(index: Int) {
        if (findViewById<View>(R.id.player_settings_content) != null) {
            val ft = supportFragmentManager.beginTransaction()
            if (null != mCurrentFrgment) {
                ft.hide(mCurrentFrgment!!)
            }
            var fragment = supportFragmentManager.findFragmentByTag(fragmentArrayList!![index].javaClass.name)

            if (null == fragment) {
                fragment = fragmentArrayList!![index]
            }
            mCurrentFrgment = fragment

            ft.replace(R.id.player_settings_content, fragment, fragment.javaClass.name)
            ft.show(fragment)
            ft.commit()
        }
    }

    override fun onNotifyActivity() {
//        setResult(AliyunPlayerSkinActivity.RESULT_OK)
        finish()
    }

    companion object {

        private val FRAGMENT_VID_PLAY = 0
        private val FRAGMENT_URL_PLAY = 1
    }
}
