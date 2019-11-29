package com.zxq.flutter_aliplayer.utils

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * assets目录文件拷贝工具类
 */
class Common private constructor(private var context: Context?) {
    private var callback: FileOperateCallback? = null
    @Volatile
    private var isSuccess: Boolean = false
    private var errorStr: String? = null
    private var threadPoolExecutor: ThreadPoolExecutor? = null
    private var srcPath: String? = null
    private var sdPath: String? = null

    private var handler: Handler? = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (callback != null) {
                if (msg.what == SUCCESS) {
                    callback!!.onSuccess()
                }
                if (msg.what == FAILED) {
                    callback!!.onFailed(msg.obj.toString())
                }
            }
            threadPoolExecutor!!.remove(runnable)
            context = null
            instance = null
        }
    }

    private val runnable = Runnable {
        copyAssetsToDst(context, srcPath, sdPath)
        if (isSuccess) {
            handler!!.obtainMessage(SUCCESS).sendToTarget()
        } else {
            handler!!.obtainMessage(FAILED, errorStr).sendToTarget()
        }
    }

    fun copyAssetsToSD(srcPath: String, sdPath: String): Common {
        this.srcPath = srcPath
        this.sdPath = sdPath

        threadPoolExecutor = ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, LinkedBlockingDeque())
        threadPoolExecutor!!.execute(Executors.defaultThreadFactory().newThread(runnable))

        return this
    }

    fun setFileOperateCallback(callback: FileOperateCallback) {
        this.callback = callback
    }

    private fun copyAssetsToDst(context: Context?, srcPath: String?, dstPath: String?) {
        try {
            val fileNames = context!!.assets.list(srcPath!!)
            if (fileNames!!.size > 0) {
                val file = File(Environment.getExternalStorageDirectory(), dstPath)
                if (!file.exists()) {
                    file.mkdirs()
                }
                for (fileName in fileNames) {
                    // assets 文件夹下的目录
                    if ("" != srcPath) {
                        copyAssetsToDst(context, srcPath + File.separator + fileName,
                                dstPath + File.separator + fileName)
                    } else {
                        // assets 文件夹
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName)
                    }
                }
            } else {
                val outFile = File(Environment.getExternalStorageDirectory(), dstPath)
                val `is` = context.assets.open(srcPath)
                val fos = FileOutputStream(outFile)
                val buffer = ByteArray(1024)
                var byteCount: Int
                byteCount = `is`.read(buffer)
                while (byteCount != -1) {
                    fos.write(buffer, 0, byteCount)
                }
                fos.flush()
                `is`.close()
                fos.close()
            }
            isSuccess = true
        } catch (e: Exception) {
            e.printStackTrace()
            errorStr = e.message
            isSuccess = false
        }

    }

    fun onDestroy() {
        if (threadPoolExecutor != null && runnable != null) {
            threadPoolExecutor!!.remove(runnable)
        }

        if (handler != null) {
            handler!!.removeMessages(SUCCESS)
            handler!!.removeMessages(FAILED)
            handler = null
        }
        instance = null
        callback = null
        context = null

    }

    interface FileOperateCallback {
        /**
         * copy success
         */
        fun onSuccess()

        /**
         * copy fail
         * @param error 错误信息
         */
        fun onFailed(error: String)
    }

    companion object {
        private var instance: Common? = null
        private val SUCCESS = 1
        private val FAILED = 0

        fun getInstance(context: Context): Common {
            if (instance == null) {
                synchronized(Common::class.java) {
                    if (instance == null) {
                        instance = Common(context)
                    }
                }
            }
            return instance!!
        }
    }

}
