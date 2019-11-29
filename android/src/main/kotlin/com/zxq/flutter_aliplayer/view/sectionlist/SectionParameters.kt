package com.zxq.flutter_aliplayer.view.sectionlist

import android.support.annotation.LayoutRes

/**
 * Class used as constructor parameters of [Section].
 */
class SectionParameters private constructor(builder: Builder) {
    @LayoutRes
    val itemResourceId: Int?
    @LayoutRes
    val headerResourceId: Int?
    @LayoutRes
    val footerResourceId: Int?
    @LayoutRes
    val loadingResourceId: Int?
    @LayoutRes
    val failedResourceId: Int?
    @LayoutRes
    val emptyResourceId: Int?
    val itemViewWillBeProvided: Boolean
    val headerViewWillBeProvided: Boolean
    val footerViewWillBeProvided: Boolean
    val loadingViewWillBeProvided: Boolean
    val failedViewWillBeProvided: Boolean
    val emptyViewWillBeProvided: Boolean

    init {
        this.itemResourceId = builder.itemResourceId
        this.headerResourceId = builder.headerResourceId
        this.footerResourceId = builder.footerResourceId
        this.loadingResourceId = builder.loadingResourceId
        this.failedResourceId = builder.failedResourceId
        this.emptyResourceId = builder.emptyResourceId
        this.itemViewWillBeProvided = builder.itemViewWillBeProvided
        this.headerViewWillBeProvided = builder.headerViewWillBeProvided
        this.footerViewWillBeProvided = builder.footerViewWillBeProvided
        this.loadingViewWillBeProvided = builder.loadingViewWillBeProvided
        this.failedViewWillBeProvided = builder.failedViewWillBeProvided
        this.emptyViewWillBeProvided = builder.emptyViewWillBeProvided

        if (itemResourceId != null && itemViewWillBeProvided) {
            throw IllegalArgumentException(
                    "itemResourceId and itemViewWillBeProvided cannot both be set")
        } else if (itemResourceId == null && !itemViewWillBeProvided) {
            throw IllegalArgumentException(
                    "Either itemResourceId or itemViewWillBeProvided must be set")
        }
        if (headerResourceId != null && headerViewWillBeProvided) {
            throw IllegalArgumentException(
                    "headerResourceId and headerViewWillBeProvided cannot both be set")
        }
        if (footerResourceId != null && footerViewWillBeProvided) {
            throw IllegalArgumentException(
                    "footerResourceId and footerViewWillBeProvided cannot both be set")
        }
        if (loadingResourceId != null && loadingViewWillBeProvided) {
            throw IllegalArgumentException(
                    "loadingResourceId and loadingViewWillBeProvided cannot both be set")
        }
        if (failedResourceId != null && failedViewWillBeProvided) {
            throw IllegalArgumentException(
                    "failedResourceId and failedViewWillBeProvided cannot both be set")
        }
        if (emptyResourceId != null && emptyViewWillBeProvided) {
            throw IllegalArgumentException(
                    "emptyResourceId and emptyViewWillBeProvided cannot both be set")
        }
    }

    /**
     * Builder of [SectionParameters].
     */
    class Builder {
        @LayoutRes
        internal var itemResourceId: Int? = null
        @LayoutRes
        internal var headerResourceId: Int? = null
        @LayoutRes
        internal var footerResourceId: Int? = null
        @LayoutRes
        internal var loadingResourceId: Int? = null
        @LayoutRes
        internal var failedResourceId: Int? = null
        @LayoutRes
        internal var emptyResourceId: Int? = null
        internal var itemViewWillBeProvided: Boolean = false
        internal var headerViewWillBeProvided: Boolean = false
        internal var footerViewWillBeProvided: Boolean = false
        internal var loadingViewWillBeProvided: Boolean = false
        internal var failedViewWillBeProvided: Boolean = false
        internal var emptyViewWillBeProvided: Boolean = false

        /**
         * Constructor with mandatory parameters of [Section] (namely none).
         */
        internal constructor() {}

        /**
         * Constructor with optional parameter for backward compatibility purposes.
         *
         * @param itemResourceId layout resource for Section's items
         *
         */
        @Deprecated("Use {@link #SectionParameters#builder} instead.")
        constructor(@LayoutRes itemResourceId: Int) {
            this.itemResourceId = itemResourceId
        }

        /**
         * Set layout resource for Section's items.
         *
         * @param itemResourceId layout resource for Section's items
         * @return this builder
         */
        fun itemResourceId(@LayoutRes itemResourceId: Int): Builder {
            this.itemResourceId = itemResourceId

            return this
        }

        /**
         * Declare that there will be no [.itemResourceId], as the view will be provided by
         * the Section's implementation directly.
         *
         * @return this builder
         */
        fun itemViewWillBeProvided(): Builder {
            itemViewWillBeProvided = true

            return this
        }

        /**
         * Set layout resource for Section's header.
         *
         * @param headerResourceId layout resource for Section's header
         * @return this builder
         */
        fun headerResourceId(@LayoutRes headerResourceId: Int): Builder {
            this.headerResourceId = headerResourceId

            return this
        }

        /**
         * Declare that there will be no [.headerResourceId], as the view will be provided by
         * the Section's implementation directly.
         *
         * @return this builder
         */
        fun headerViewWillBeProvided(): Builder {
            headerViewWillBeProvided = true

            return this
        }

        /**
         * Set layout resource for Section's footer.
         *
         * @param footerResourceId layout resource for Section's footer
         * @return this builder
         */
        fun footerResourceId(@LayoutRes footerResourceId: Int): Builder {
            this.footerResourceId = footerResourceId

            return this
        }

        /**
         * Declare that there will be no [.footerResourceId], as the view will be provided by
         * the Section's implementation directly.
         *
         * @return this builder
         */
        fun footerViewWillBeProvided(): Builder {
            footerViewWillBeProvided = true

            return this
        }

        /**
         * Set layout resource for Section's loading state.
         *
         * @param loadingResourceId layout resource for Section's loading state
         * @return this builder
         */
        fun loadingResourceId(@LayoutRes loadingResourceId: Int): Builder {
            this.loadingResourceId = loadingResourceId

            return this
        }

        /**
         * Declare that there will be no [.loadingResourceId], as the view will be provided by
         * the Section's implementation directly.
         *
         * @return this builder
         */
        fun loadingViewWillBeProvided(): Builder {
            loadingViewWillBeProvided = true

            return this
        }

        /**
         * Set layout resource for Section's failed state.
         *
         * @param failedResourceId layout resource for Section's failed state
         * @return this builder
         */
        fun failedResourceId(@LayoutRes failedResourceId: Int): Builder {
            this.failedResourceId = failedResourceId

            return this
        }

        /**
         * Declare that there will be no [.failedResourceId], as the view will be provided by
         * the Section's implementation directly.
         *
         * @return this builder
         */
        fun failedViewWillBeProvided(): Builder {
            failedViewWillBeProvided = true

            return this
        }

        /**
         * Set layout resource for Section's empty state.
         *
         * @param emptyResourceId layout resource for Section's empty state
         * @return this builder
         */
        fun emptyResourceId(@LayoutRes emptyResourceId: Int): Builder {
            this.emptyResourceId = emptyResourceId

            return this
        }

        /**
         * Declare that there will be no [.emptyResourceId], as the view will be provided by
         * the Section's implementation directly.
         *
         * @return this builder
         */
        fun emptyViewWillBeProvided(): Builder {
            emptyViewWillBeProvided = true

            return this
        }

        /**
         * Build an instance of SectionParameters.
         *
         * @return an instance of SectionParameters
         */
        fun build(): SectionParameters {
            return SectionParameters(this)
        }
    }

    companion object {

        /**
         * Builder static factory method with mandatory parameters of [Section] (namely none).
         */
        fun builder(): Builder {
            return Builder()
        }
    }
}
