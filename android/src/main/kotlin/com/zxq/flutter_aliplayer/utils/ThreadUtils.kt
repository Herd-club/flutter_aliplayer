package com.zxq.flutter_aliplayer.utils

import android.os.Handler
import android.os.Looper
import android.util.Log

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * android 主线程、子线程切换、主线程延时任务工具类
 *
 * 1.通过[Looper.getMainLooper]获取主线程looper对象，创建mainHandler
 * 2.线程池参数使用AsyncTask的配置，AsyncTask是android sdk 26的版本
 * 3.切换主线程任务，延时切换主线程
 * 4.使用线程池创建子线程，执行简单的异步任务
 */
object ThreadUtils {

    private val sMainHandler = Handler(Looper.getMainLooper())
    private val TAG = ThreadUtils::class.java.name

    /**
     * 线程池的参数采用AsyncTask的配置 -- android 26
     */
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private val CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4))
    private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
    private val KEEP_ALIVE_SECONDS = 30
    private val POOL_WORK_QUEUE = LinkedBlockingQueue<Runnable>(128)
    private val THREAD_FACTORY = object : ThreadFactory {
        private val mCount = AtomicInteger(1)

        override fun newThread(r: Runnable): Thread {
            return Thread(r, "ThreadUtils #" + mCount.getAndIncrement())
        }
    }
    /**
     * An [Executor] that can be used to execute tasks in parallel.
     */
    private val THREAD_POOL_EXECUTOR: ThreadPoolExecutor

    init {
        val threadPoolExecutor = ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS.toLong(), TimeUnit.SECONDS,
                POOL_WORK_QUEUE, THREAD_FACTORY)
        threadPoolExecutor.allowCoreThreadTimeOut(true)
        THREAD_POOL_EXECUTOR = threadPoolExecutor
    }

    /**
     * 延时切换到主线程
     * @param runnable Runnable
     * @param delayed 时长 Millis
     */
    @JvmOverloads
    fun runOnUiThread(runnable: Runnable, delayed: Long = 0) {
        sMainHandler.postDelayed(runnable, delayed)
    }

    /**
     * 从线程池中创建子线程执行异步任务
     * 在任务数超过128，或者线程池Shutdown时将跳过这条任务
     * @param runnable Runnable
     */
    fun runOnSubThread(runnable: Runnable) {

        if (THREAD_POOL_EXECUTOR.queue.size == 128 || THREAD_POOL_EXECUTOR.isShutdown) {
            Log.e(TAG, "线程池爆满警告，请查看是否开启了过多的耗时线程")
            return
        }
        THREAD_POOL_EXECUTOR.execute(runnable)
    }
}
/**
 * 切换到主线程
 * @param runnable Runnable
 */
