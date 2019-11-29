package com.zxq.flutter_aliplayer.constants

/**
 * 对外提供的配置文件
 */
class AlivcPlayerConfig {

    var vid: String? = null
        private set

    class Builder {

        private val alivcPlayerConfig: AlivcPlayerConfig

        private val vid: String? = null

        init {
            alivcPlayerConfig = AlivcPlayerConfig()
        }

        fun vid(vid: String): Builder {
            alivcPlayerConfig.vid = vid
            return this
        }

        fun build(): AlivcPlayerConfig {
            return alivcPlayerConfig
        }
    }
}
