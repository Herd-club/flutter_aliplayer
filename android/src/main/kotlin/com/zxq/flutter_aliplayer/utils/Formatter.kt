package com.zxq.flutter_aliplayer.utils

import android.text.TextUtils

import java.util.Calendar
import java.util.Locale

/**
 * @Author: lifujun@alibaba-inc.com
 * @Date: 2016/12/29.
 * @Description:
 */

object Formatter {

    fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 60 / 60
        var timeStr = ""
        if (hours > 9) {
            timeStr += "$hours:"
        } else if (hours > 0) {
            timeStr += "0$hours:"
        }
        if (minutes > 9) {
            timeStr += "$minutes:"
        } else if (minutes > 0) {
            timeStr += "0$minutes:"
        } else {
            timeStr += "00:"
        }
        if (seconds > 9) {
            timeStr += seconds
        } else if (seconds > 0) {
            timeStr += "0$seconds"
        } else {
            timeStr += "00"
        }

        return timeStr
    }


    fun formatDate(seconds: Long): String {
        var finalStr = ""
        val mills = seconds * 1000
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = mills
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        finalStr += (if (hour < 10) "0$hour" else hour).toString() + ":"
        val minute = calendar.get(Calendar.MINUTE)
        finalStr += (if (minute < 10) "0$minute" else minute).toString() + ":"
        val second = calendar.get(Calendar.SECOND)
        finalStr += if (second < 10) "0$second" else second

        return finalStr

    }

    /**
     * double类型转换为日期  mm:ss
     * @param time
     * @return
     */
    fun double2Date(time: Double): String {
        var lTime = time.toLong()
        lTime = lTime - 28800
        val s = formatDate(lTime)
        return s.substring(3)

    }

    /**
     * 把 00:00:00 格式转成时间戳
     * @param formatTime    00:00:00 时间格式
     * @return 时间戳(毫秒)
     */
    fun getIntTime(formatTime: String): Int {
        if (TextUtils.isEmpty(formatTime)) {
            return 0
        }

        val tmp = formatTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (tmp.size < 3) {
            return 0
        }
        val second = Integer.valueOf(tmp[0]) * 3600 + Integer.valueOf(tmp[1]) * 60 + Integer.valueOf(tmp[2])

        return second * 1000
    }

    /**
     * 把时间戳转换成 00:00:00 格式
     * @param timeMs    时间戳
     * @return 00:00:00 时间格式
     */
    fun getStringTime(timeMs: Int): String {
        val formatBuilder = StringBuilder()
        val formatter = java.util.Formatter(formatBuilder, Locale.getDefault())

        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600

        formatBuilder.setLength(0)
        return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString()
    }
}
