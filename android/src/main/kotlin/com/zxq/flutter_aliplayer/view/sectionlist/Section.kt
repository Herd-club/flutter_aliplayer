package com.zxq.flutter_aliplayer.view.sectionlist

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Abstract Section to be used with [SectionedRecyclerViewAdapter].
 */
abstract class Section
/**
 * Create a Section object based on [SectionParameters].
 *
 * @param sectionParameters section parameters
 */
(sectionParameters: SectionParameters) {

    /**
     * Return the current State of this Section.
     *
     * @return current state of this section
     */
    /**
     * Set the State of this Section.
     *
     * @param state state of this section
     */
    var state = State.LOADED
        set(state) {
            when (state) {
                Section.State.LOADING -> if (loadingResourceId == null && !isLoadingViewWillBeProvided) {
                    throw IllegalStateException(
                            "Resource id for 'loading state' should be provided or 'loadingViewWillBeProvided' should be set")
                }
                Section.State.FAILED -> if (failedResourceId == null && !isFailedViewWillBeProvided) {
                    throw IllegalStateException("Resource id for 'failed state' should be provided or 'failedViewWillBeProvided' should be set")
                }
                Section.State.EMPTY -> if (emptyResourceId == null && !isEmptyViewWillBeProvided) {
                    throw IllegalStateException("Resource id for 'empty state' should be provided or 'emptyViewWillBeProvided' should be set")
                }
                else -> {
                }
            }

            field = state
        }

    /**
     * Check if this Section is visible.
     *
     * @return true if this Section is visible
     */
    /**
     * Set if this Section is visible.
     *
     * @param visible true if this Section is visible
     */
    var isVisible = true

    private var hasHeader = false
    private var hasFooter = false

    /**
     * Return the layout resource id of the item.
     *
     * @return layout resource id of the item
     */
    @LayoutRes
    val itemResourceId: Int?
    /**
     * Return the layout resource id of the header.
     *
     * @return layout resource id of the header
     */
    @LayoutRes
    val headerResourceId: Int?
    /**
     * Return the layout resource id of the footer.
     *
     * @return layout resource id of the footer
     */
    @LayoutRes
    val footerResourceId: Int?
    /**
     * Return the layout resource id of the loading view.
     *
     * @return layout resource id of the loading view
     */
    @LayoutRes
    val loadingResourceId: Int?
    /**
     * Return the layout resource id of the failed view.
     *
     * @return layout resource id of the failed view
     */
    @LayoutRes
    val failedResourceId: Int?
    /**
     * Return the layout resource id of the empty view.
     *
     * @return layout resource id of the empty view
     */
    @LayoutRes
    val emptyResourceId: Int?
    /**
     * Return whether the item view is provided through [.getItemView].
     * If false, the item view is inflated using the resource from [.getItemResourceId].
     *
     * @return whether the item view is provided through [.getItemView].
     */
    val isItemViewWillBeProvided: Boolean
    /**
     * Return whether the header view is provided through [.getHeaderView].
     * If false, the header view is inflated using the resource from
     * [.getHeaderResourceId].
     *
     * @return whether the header view is provided through [.getHeaderView].
     */
    val isHeaderViewWillBeProvided: Boolean
    /**
     * Return whether the footer view is provided through [.getFooterView].
     * If false, the footer view is inflated using the resource from
     * [.getFooterResourceId].
     *
     * @return whether the footer view is provided through [.getFooterView].
     */
    val isFooterViewWillBeProvided: Boolean
    /**
     * Return whether the loading view is provided through [.getLoadingView].
     * If false, the loading view is inflated using the resource from
     * [.getLoadingResourceId].
     *
     * @return whether the loading view is provided through [.getLoadingView].
     */
    val isLoadingViewWillBeProvided: Boolean
    /**
     * Return whether the failed view is provided through [.getFailedView].
     * If false, the failed view is inflated using the resource from
     * [.getFailedResourceId].
     *
     * @return whether the failed view is provided through [.getFailedView].
     */
    val isFailedViewWillBeProvided: Boolean
    /**
     * Return whether the empty view is provided through [.getEmptyView].
     * If false, the empty view is inflated using the resource from
     * [.getEmptyResourceId].
     *
     * @return whether the empty view is provided through [.getEmptyView].
     */
    val isEmptyViewWillBeProvided: Boolean

    /**
     * Return the total of items of this Section, including content items (according to the section
     * state) plus header and footer.
     *
     * @return total of items of this section
     */
    val sectionItemsTotal: Int
        get() {
            val contentItemsTotal: Int
            when (this.state) {
                Section.State.LOADING -> contentItemsTotal = 1
                Section.State.FAILED -> contentItemsTotal = 1
                Section.State.EMPTY -> contentItemsTotal = 1
                else -> throw IllegalStateException("Invalid state")
            }

            return contentItemsTotal + (if (hasHeader) 1 else 0) + if (hasFooter) 1 else 0
        }

    /**
     * Return the total of items of this Section.
     *
     * @return total of items of this Section
     */
    abstract val contentItemsTotal: Int

    enum class State {
        /**
         * LOADING: 加载中
         * LOADED: 加载完成
         * FAILED: 加载失败
         * EMPTY: 空数据
         */
        LOADING,
        LOADED,
        FAILED,
        EMPTY
    }

    init {
        this.itemResourceId = sectionParameters.itemResourceId
        this.headerResourceId = sectionParameters.headerResourceId
        this.footerResourceId = sectionParameters.footerResourceId
        this.loadingResourceId = sectionParameters.loadingResourceId
        this.failedResourceId = sectionParameters.failedResourceId
        this.emptyResourceId = sectionParameters.emptyResourceId
        this.isItemViewWillBeProvided = sectionParameters.itemViewWillBeProvided
        this.isHeaderViewWillBeProvided = sectionParameters.headerViewWillBeProvided
        this.isFooterViewWillBeProvided = sectionParameters.footerViewWillBeProvided
        this.isLoadingViewWillBeProvided = sectionParameters.loadingViewWillBeProvided
        this.isFailedViewWillBeProvided = sectionParameters.failedViewWillBeProvided
        this.isEmptyViewWillBeProvided = sectionParameters.emptyViewWillBeProvided

        this.hasHeader = this.headerResourceId != null || this.isHeaderViewWillBeProvided
        this.hasFooter = this.footerResourceId != null || this.isFooterViewWillBeProvided
    }

    /**
     * Check if this Section has a header.
     *
     * @return true if this Section has a header
     */
    fun hasHeader(): Boolean {
        return hasHeader
    }

    /**
     * Set if this Section has header.
     *
     * @param hasHeader true if this Section has a header
     */
    fun setHasHeader(hasHeader: Boolean) {
        this.hasHeader = hasHeader
    }

    /**
     * Check if this Section has a footer.
     *
     * @return true if this Section has a footer
     */
    fun hasFooter(): Boolean {
        return hasFooter
    }

    /**
     * Set if this Section has footer.
     *
     * @param hasFooter true if this Section has a footer
     */
    fun setHasFooter(hasFooter: Boolean) {
        this.hasFooter = hasFooter
    }

    /**
     * Bind the data to the ViewHolder for the Content of this Section, that can be the Items,
     * Loading view or Failed view, depending on the current state of the section.
     *
     * @param holder   ViewHolder for the Content of this Section
     * @param position position of the item in the Section, not in the RecyclerView
     */
    fun onBindContentViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (this.state) {
            Section.State.LOADING -> onBindLoadingViewHolder(holder)
            Section.State.LOADED -> onBindItemViewHolder(holder, position)
            Section.State.FAILED -> onBindFailedViewHolder(holder)
            Section.State.EMPTY -> onBindEmptyViewHolder(holder)
            else -> throw IllegalStateException("Invalid state")
        }
    }

    /**
     * Creates the View for a single Item. This must be implemented if and only if
     * [.isItemViewWillBeProvided] is true.
     *
     * @param parent The parent view. Note that there is no need to attach the new view.
     * @return View for an Item of this Section.
     */
    fun getItemView(parent: ViewGroup): View {
        throw UnsupportedOperationException(
                "You need to implement getItemView() if you set itemViewWillBeProvided")
    }

    /**
     * Return the ViewHolder for a single Item of this Section.
     *
     * @param view View created by getItemView or inflated resource returned by getItemResourceId
     * @return ViewHolder for the Item of this Section
     */
    abstract fun getItemViewHolder(view: View): RecyclerView.ViewHolder

    /**
     * Bind the data to the ViewHolder for an Item of this Section.
     *
     * @param holder   ViewHolder for the Item of this Section
     * @param position position of the item in the Section, not in the RecyclerView
     */
    abstract fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    /**
     * Creates the View for the Header. This must be implemented if and only if
     * [.isHeaderViewWillBeProvided] is true.
     *
     * @param parent The parent view. Note that there is no need to attach the new view.
     * @return View for the Header of this Section.
     */
    fun getHeaderView(parent: ViewGroup): View {
        throw UnsupportedOperationException(
                "You need to implement getHeaderView() if you set headerViewWillBeProvided")
    }

    /**
     * Return the ViewHolder for the Header of this Section.
     *
     * @param view View inflated by resource returned by getHeaderResourceId
     * @return ViewHolder for the Header of this Section
     */
    open fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return SectionedRecyclerViewAdapter.EmptyViewHolder(view)
    }

    /**
     * Bind the data to the ViewHolder for the Header of this Section.
     *
     * @param holder ViewHolder for the Header of this Section
     */
    open fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        // Nothing to bind here.
    }

    /**
     * Creates the View for the Footer. This must be implemented if and only if
     * [.isFooterViewWillBeProvided] is true.
     *
     * @param parent The parent view. Note that there is no need to attach the new view.
     * @return View for the Footer of this Section.
     */
    fun getFooterView(parent: ViewGroup): View {
        throw UnsupportedOperationException(
                "You need to implement getFooterView() if you set footerViewWillBeProvided")
    }

    /**
     * Return the ViewHolder for the Footer of this Section.
     *
     * @param view View inflated by resource returned by getFooterResourceId
     * @return ViewHolder for the Footer of this Section
     */
    fun getFooterViewHolder(view: View): RecyclerView.ViewHolder {
        return SectionedRecyclerViewAdapter.EmptyViewHolder(view)
    }

    /**
     * Bind the data to the ViewHolder for the Footer of this Section.
     *
     * @param holder ViewHolder for the Footer of this Section
     */
    fun onBindFooterViewHolder(holder: RecyclerView.ViewHolder) {
        // Nothing to bind here.
    }

    /**
     * Creates the View for the Loading state. This must be implemented if and only if
     * [.isLoadingViewWillBeProvided] is true.
     *
     * @param parent The parent view. Note that there is no need to attach the new view.
     * @return View for the Loading state of this Section.
     */
    fun getLoadingView(parent: ViewGroup): View {
        throw UnsupportedOperationException(
                "You need to implement getLoadingView() if you set loadingViewWillBeProvided")
    }

    /**
     * Return the ViewHolder for the Loading state of this Section.
     *
     * @param view View inflated by resource returned by getItemResourceId
     * @return ViewHolder for the Loading state of this Section
     */
    open fun getLoadingViewHolder(view: View): RecyclerView.ViewHolder {
        return SectionedRecyclerViewAdapter.EmptyViewHolder(view)
    }

    /**
     * Bind the data to the ViewHolder for Loading state of this Section.
     *
     * @param holder ViewHolder for the Loading state of this Section
     */
    open fun onBindLoadingViewHolder(holder: RecyclerView.ViewHolder) {
        // Nothing to bind here.
    }

    /**
     * Creates the View for the Failed state. This must be implemented if and only if
     * [.isFailedViewWillBeProvided] is true.
     *
     * @param parent The parent view. Note that there is no need to attach the new view.
     * @return View for the Failed state of this Section.
     */
    fun getFailedView(parent: ViewGroup): View {
        throw UnsupportedOperationException(
                "You need to implement getFailedView() if you set failedViewWillBeProvided")
    }

    /**
     * Return the ViewHolder for the Failed state of this Section.
     *
     * @param view View inflated by resource returned by getItemResourceId
     * @return ViewHolder for the Failed of this Section
     */
    open fun getFailedViewHolder(view: View): RecyclerView.ViewHolder {
        return SectionedRecyclerViewAdapter.EmptyViewHolder(view)
    }

    /**
     * Bind the data to the ViewHolder for the Failed state of this Section.
     *
     * @param holder ViewHolder for the Failed state of this Section
     */
    open fun onBindFailedViewHolder(holder: RecyclerView.ViewHolder) {
        // Nothing to bind here.
    }

    /**
     * Creates the View for the Empty state. This must be implemented if and only if
     * [.isEmptyViewWillBeProvided] is true.
     *
     * @param parent The parent view. Note that there is no need to attach the new view.
     * @return View for the Empty state of this Section.
     */
    fun getEmptyView(parent: ViewGroup): View {
        throw UnsupportedOperationException(
                "You need to implement getEmptyView() if you set emptyViewWillBeProvided")
    }

    /**
     * Return the ViewHolder for the Empty state of this Section.
     *
     * @param view View inflated by resource returned by getItemResourceId
     * @return ViewHolder for the Empty of this Section
     */
    open fun getEmptyViewHolder(view: View): RecyclerView.ViewHolder {
        return SectionedRecyclerViewAdapter.EmptyViewHolder(view)
    }

    /**
     * Bind the data to the ViewHolder for the Empty state of this Section.
     *
     * @param holder ViewHolder for the Empty state of this Section
     */
    open fun onBindEmptyViewHolder(holder: RecyclerView.ViewHolder) {
        // Nothing to bind here.
    }
}
