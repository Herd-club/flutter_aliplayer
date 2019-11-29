package com.zxq.flutter_aliplayer

import android.app.Activity
import android.content.Intent
import com.aliyun.player.nativeclass.PlayerConfig
import com.zxq.flutter_aliplayer.activity.AliyunPlayerSkinActivity
import com.zxq.flutter_aliplayer.constants.PlayParameter
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar


class FlutterAliplayerPlugin(val activity: Activity): MethodCallHandler {

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "flutter_aliplayer")
      channel.setMethodCallHandler(FlutterAliplayerPlugin(registrar.activity()))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when(call.method) {
      "play" -> showPlayer(call, result)
    }
  }

  private fun showPlayer(call: MethodCall, result: Result) {
    val map = call.arguments as Map<String, String>
    PlayParameter.PLAY_PARAM_URL = map["url"].toString()

    val intent = Intent(activity, AliyunPlayerSkinActivity::class.java)
    activity.startActivityForResult(intent, 100)
  }
}
