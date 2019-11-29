package com.zxq.flutter_aliplayer.view.sectionlist

import java.util.LinkedHashMap
import java.util.UUID

import android.support.annotation.LayoutRes
import android.support.annotation.VisibleForTesting
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zxq.flutter_aliplayer.view.sectionlist.Section.State

/**
 * A custom RecyclerView Adapter that allows [Sections][Section] to be added to it.
 * Sections are displayed in the same order they were added.
 */
class SectionedRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sections: MutableMap<String, Section>
    private val sectionViewTypeNumbers: MutableMap<String, Int>

    private var viewTypeCount = 0

    init {
        sections = LinkedHashMap()
        sectionViewTypeNumbers = LinkedHashMap()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null

        for ((key, value) in sectionViewTypeNumbers) {
            if (viewType >= value && viewType < value + VIEW_TYPE_QTY) {

                val section = sections[key]
                val sectionViewType = viewType - value

                when (sectionViewType) {
                    VIEW_TYPE_HEADER -> viewHolder = getHeaderViewHolder(parent, section!!)
                    VIEW_TYPE_FOOTER -> viewHolder = getFooterViewHolder(parent, section!!)
                    VIEW_TYPE_ITEM_LOADED -> viewHolder = getItemViewHolder(parent, section!!)
                    VIEW_TYPE_LOADING -> viewHolder = getLoadingViewHolder(parent, section!!)
                    VIEW_TYPE_FAILED -> viewHolder = getFailedViewHolder(parent, section!!)
                    VIEW_TYPE_EMPTY -> viewHolder = getEmptyViewHolder(parent, section!!)
                    else -> throw IllegalArgumentException("Invalid viewType")
                }
            }
        }

        return viewHolder!!
    }

    @VisibleForTesting
    internal fun inflate(@LayoutRes layoutResourceId: Int, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(layoutResourceId, parent, false)
    }

    private fun getItemViewHolder(parent: ViewGroup, section: Section): RecyclerView.ViewHolder {
        val view: View?
        if (section.isItemViewWillBeProvided) {
            view = section.getItemView(parent)
            if (view == null) {
                throw NullPointerException("Section.getItemView() returned null")
            }
        } else {
            val resId = section.itemResourceId
                    ?: throw NullPointerException("Missing 'item' resource id")
            view = inflate(resId, parent)
        }
        return section.getItemViewHolder(view)
    }

    private fun getHeaderViewHolder(parent: ViewGroup, section: Section): RecyclerView.ViewHolder {
        val view: View?
        if (section.isHeaderViewWillBeProvided) {
            view = section.getHeaderView(parent)
            if (view == null) {
                throw NullPointerException("Section.getHeaderView() returned null")
            }
        } else {
            val resId = section.headerResourceId
                    ?: throw NullPointerException("Missing 'header' resource id")
            view = inflate(resId, parent)
        }
        return section.getHeaderViewHolder(view)
    }

    private fun getFooterViewHolder(parent: ViewGroup, section: Section): RecyclerView.ViewHolder {
        val view: View?
        if (section.isFooterViewWillBeProvided) {
            view = section.getFooterView(parent)
            if (view == null) {
                throw NullPointerException("Section.getFooterView() returned null")
            }
        } else {
            val resId = section.footerResourceId
                    ?: throw NullPointerException("Missing 'footer' resource id")
            view = inflate(resId, parent)
        }
        return section.getFooterViewHolder(view)
    }

    private fun getLoadingViewHolder(parent: ViewGroup, section: Section): RecyclerView.ViewHolder {
        val view: View?
        if (section.isLoadingViewWillBeProvided) {
            view = section.getLoadingView(parent)
            if (view == null) {
                throw NullPointerException("Section.getLoadingView() returned null")
            }
        } else {
            val resId = section.loadingResourceId
                    ?: throw NullPointerException("Missing 'loading' resource id")
            view = inflate(resId, parent)
        }
        return section.getLoadingViewHolder(view)
    }

    private fun getFailedViewHolder(parent: ViewGroup, section: Section): RecyclerView.ViewHolder {
        val view: View?
        if (section.isFailedViewWillBeProvided) {
            view = section.getFailedView(parent)
            if (view == null) {
                throw NullPointerException("Section.getFailedView() returned null")
            }
        } else {
            val resId = section.failedResourceId
                    ?: throw NullPointerException("Missing 'failed' resource id")
            view = inflate(resId, parent)
        }
        return section.getFailedViewHolder(view)
    }

    private fun getEmptyViewHolder(parent: ViewGroup, section: Section): RecyclerView.ViewHolder {
        val view: View?
        if (section.isEmptyViewWillBeProvided) {
            view = section.getEmptyView(parent)
            if (view == null) {
                throw NullPointerException("Section.getEmptyView() returned null")
            }
        } else {
            val resId = section.emptyResourceId
                    ?: throw NullPointerException("Missing 'empty' resource id")
            view = inflate(resId, parent)
        }
        return section.getEmptyViewHolder(view)
    }

    /**
     * Add a section to this recyclerview.
     *
     * @param tag     unique identifier of the section
     * @param section section to be added
     */
    fun addSection(tag: String, section: Section) {
        this.sections[tag] = section
        this.sectionViewTypeNumbers[tag] = viewTypeCount
        viewTypeCount += VIEW_TYPE_QTY
    }

    /**
     * Add a section to this recyclerview with a random tag.
     *
     * @param section section to be added
     * @return generated tag
     */
    fun addSection(section: Section): String {
        val tag = UUID.randomUUID().toString()

        addSection(tag, section)

        return tag
    }

    /**
     * Return the section with the tag provided.
     *
     * @param tag unique identifier of the section
     * @return section
     */
    fun getSection(tag: String): Section? {
        return this.sections[tag]
    }

    /**
     * Remove section from this recyclerview.
     *
     * @param section section to be removed
     */
    fun removeSection(section: Section) {
        var tag: String? = null
        for ((key, value) in this.sections) {
            if (value === section) {
                tag = key
            }
        }

        if (tag != null) {
            this.removeSection(tag)
        }
    }

    /**
     * Remove section from this recyclerview.
     *
     * @param tag unique identifier of the section
     */
    fun removeSection(tag: String) {
        this.sections.remove(tag)
        this.sectionViewTypeNumbers.remove(tag)
    }

    /**
     * Remove all sections from this recyclerview.
     */
    fun removeAllSections() {
        this.sections.clear()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var currentPos = 0

        for ((_, section) in sections) {

            // ignore invisible sections
            if (!section.isVisible) {
                continue
            }

            val sectionTotal = section.sectionItemsTotal

            // check if position is in this section
            if (position >= currentPos && position <= currentPos + sectionTotal - 1) {

                if (section.hasHeader()) {
                    if (position == currentPos) {
                        // delegate the binding to the section header
                        getSectionForPosition(position).onBindHeaderViewHolder(holder)
                        return
                    }
                }

                if (section.hasFooter()) {
                    if (position == currentPos + sectionTotal - 1) {
                        // delegate the binding to the section header
                        getSectionForPosition(position).onBindFooterViewHolder(holder)
                        return
                    }
                }

                // delegate the binding to the section content
                getSectionForPosition(position).onBindContentViewHolder(holder, getPositionInSection(position))
                return
            }

            currentPos += sectionTotal
        }

        throw IndexOutOfBoundsException("Invalid position")
    }

    override fun getItemCount(): Int {
        var count = 0

        for ((_, section) in sections) {

            // ignore invisible sections
            if (!section.isVisible) {
                continue
            }

            count += section.sectionItemsTotal
        }

        return count
    }

    override fun getItemViewType(position: Int): Int {
        /*
         Each Section has 6 "viewtypes":
         1) header
         2) footer
         3) items
         4) loading
         5) load failed
         6) empty
         */
        var currentPos = 0

        for ((key, section) in sections) {

            // ignore invisible sections
            if (!section.isVisible) {
                continue
            }

            val sectionTotal = section.sectionItemsTotal

            // check if position is in this section
            if (position >= currentPos && position <= currentPos + sectionTotal - 1) {

                val viewType = sectionViewTypeNumbers[key]!!

                if (section.hasHeader()) {
                    if (position == currentPos) {
                        return viewType
                    }
                }

                if (section.hasFooter()) {
                    if (position == currentPos + sectionTotal - 1) {
                        return viewType + 1
                    }
                }

                when (section.state) {
                    Section.State.LOADED -> return viewType + 2
                    Section.State.LOADING -> return viewType + 3
                    Section.State.FAILED -> return viewType + 4
                    Section.State.EMPTY -> return viewType + 5
                    else -> throw IllegalStateException("Invalid state")
                }
            }

            currentPos += sectionTotal
        }

        throw IndexOutOfBoundsException("Invalid position")
    }

    /**
     * Returns the Section ViewType of an item based on the position in the adapter.
     *
     * @param position position in the adapter
     * @return one of the view types:
     *
     *  * SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER
     *  * SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER
     *  * SectionedRecyclerViewAdapter.VIEW_TYPE_ITEM_LOADED
     *  * SectionedRecyclerViewAdapter.VIEW_TYPE_LOADING
     *  * SectionedRecyclerViewAdapter.VIEW_TYPE_FAILED
     *  * SectionedRecyclerViewAdapter.VIEW_TYPE_EMPTY
     *
     */
    fun getSectionItemViewType(position: Int): Int {
        val viewType = getItemViewType(position)

        return viewType % VIEW_TYPE_QTY
    }

    /**
     * Returns the Section object for a position in the adapter.
     *
     * @param position position in the adapter
     * @return Section object for that position
     */
    fun getSectionForPosition(position: Int): Section {

        var currentPos = 0

        for ((_, section) in sections) {

            // ignore invisible sections
            if (!section.isVisible) {
                continue
            }

            val sectionTotal = section.sectionItemsTotal

            // check if position is in this section
            if (position >= currentPos && position <= currentPos + sectionTotal - 1) {
                return section
            }

            currentPos += sectionTotal
        }

        throw IndexOutOfBoundsException("Invalid position")
    }


    @Deprecated("Use {@link #getPositionInSection} instead.")
    fun getSectionPosition(position: Int): Int {
        return getPositionInSection(position)
    }

    /**
     * Return the item position relative to the section.
     *
     * @param position position of the item in the adapter
     * @return position of the item in the section
     */
    fun getPositionInSection(position: Int): Int {
        var currentPos = 0

        for ((_, section) in sections) {

            // ignore invisible sections
            if (!section.isVisible) {
                continue
            }

            val sectionTotal = section.sectionItemsTotal

            // check if position is in this section
            if (position >= currentPos && position <= currentPos + sectionTotal - 1) {
                return position - currentPos - if (section.hasHeader()) 1 else 0
            }

            currentPos += sectionTotal
        }

        throw IndexOutOfBoundsException("Invalid position")
    }

    /**
     * Return the section position in the adapter.
     *
     * @param tag unique identifier of the section
     * @return position of the section in the adapter
     */
    fun getSectionPosition(tag: String): Int {
        val section = getValidSectionOrThrowException(tag)

        return getSectionPosition(section)
    }

    /**
     * Return the section position in the adapter.
     *
     * @param section a visible section of this adapter
     * @return position of the section in the adapter
     */
    fun getSectionPosition(section: Section): Int {
        var currentPos = 0

        for ((_, loopSection) in sections) {

            // ignore invisible sections
            if (!loopSection.isVisible) {
                continue
            }

            if (loopSection === section) {
                return currentPos
            }

            val sectionTotal = loopSection.sectionItemsTotal

            currentPos += sectionTotal
        }

        throw IllegalArgumentException("Invalid section")
    }

    /**
     * Helper method that receives position in relation to the section, and returns the position in
     * the adapter.
     *
     * @param tag      unique identifier of the section
     * @param position position of the item in the section
     * @return position of the item in the adapter
     */
    fun getPositionInAdapter(tag: String, position: Int): Int {
        val section = getValidSectionOrThrowException(tag)

        return getPositionInAdapter(section, position)
    }

    /**
     * Helper method that receives position in relation to the section, and returns the position in
     * the adapter.
     *
     * @param section  a visible section of this adapter
     * @param position position of the item in the section
     * @return position of the item in the adapter
     */
    fun getPositionInAdapter(section: Section, position: Int): Int {
        return getSectionPosition(section) + (if (section.hasHeader()) 1 else 0) + position
    }

    /**
     * Helper method that returns the position of header in the adapter.
     *
     * @param tag unique identifier of the section
     * @return position of the header in the adapter
     */
    fun getHeaderPositionInAdapter(tag: String): Int {
        val section = getValidSectionOrThrowException(tag)

        return getHeaderPositionInAdapter(section)
    }

    /**
     * Helper method that returns the position of header in the adapter.
     *
     * @param section a visible section of this adapter
     * @return position of the header in the adapter
     */
    fun getHeaderPositionInAdapter(section: Section): Int {
        if (!section.hasHeader()) {
            throw IllegalStateException("Section doesn't have a header")
        }

        return getSectionPosition(section)
    }

    /**
     * Helper method that returns the position of footer in the adapter.
     *
     * @param tag unique identifier of the section
     * @return position of the footer in the adapter
     */
    fun getFooterPositionInAdapter(tag: String): Int {
        val section = getValidSectionOrThrowException(tag)

        return getFooterPositionInAdapter(section)
    }

    /**
     * Helper method that returns the position of header in the adapter.
     *
     * @param section a visible section of this adapter
     * @return position of the footer in the adapter
     */
    fun getFooterPositionInAdapter(section: Section): Int {
        if (!section.hasFooter()) {
            throw IllegalStateException("Section doesn't have a footer")
        }

        return getSectionPosition(section) + section.sectionItemsTotal - 1
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemInserted].
     *
     * @param tag      unique identifier of the section
     * @param position position of the item in the section
     */
    fun notifyItemInsertedInSection(tag: String, position: Int) {
        callSuperNotifyItemInserted(getPositionInAdapter(tag, position))
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemInserted].
     *
     * @param section  a visible section of this adapter
     * @param position position of the item in the section
     */
    fun notifyItemInsertedInSection(section: Section, position: Int) {
        callSuperNotifyItemInserted(getPositionInAdapter(section, position))
    }

    @VisibleForTesting
    internal fun callSuperNotifyItemInserted(position: Int) {
        super.notifyItemInserted(position)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemRangeInserted].
     *
     * @param tag           unique identifier of the section
     * @param positionStart position of the first item that was inserted in the section
     * @param itemCount     number of items inserted in the section
     */
    fun notifyItemRangeInsertedInSection(tag: String, positionStart: Int, itemCount: Int) {
        callSuperNotifyItemRangeInserted(getPositionInAdapter(tag, positionStart), itemCount)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemRangeInserted].
     *
     * @param section       a visible section of this adapter
     * @param positionStart position of the first item that was inserted in the section
     * @param itemCount     number of items inserted in the section
     */
    fun notifyItemRangeInsertedInSection(section: Section, positionStart: Int, itemCount: Int) {
        callSuperNotifyItemRangeInserted(getPositionInAdapter(section, positionStart), itemCount)
    }

    @VisibleForTesting
    internal fun callSuperNotifyItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.notifyItemRangeInserted(positionStart, itemCount)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemRemoved].
     *
     * @param tag      unique identifier of the section
     * @param position position of the item in the section
     */
    fun notifyItemRemovedFromSection(tag: String, position: Int) {
        callSuperNotifyItemRemoved(getPositionInAdapter(tag, position))
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemRemoved].
     *
     * @param section  a visible section of this adapter
     * @param position position of the item in the section
     */
    fun notifyItemRemovedFromSection(section: Section, position: Int) {
        callSuperNotifyItemRemoved(getPositionInAdapter(section, position))
    }

    @VisibleForTesting
    internal fun callSuperNotifyItemRemoved(position: Int) {
        super.notifyItemRemoved(position)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemRangeRemoved].
     *
     * @param tag           unique identifier of the section
     * @param positionStart previous position of the first item that was removed from the section
     * @param itemCount     number of items removed from the section
     */
    fun notifyItemRangeRemovedFromSection(tag: String, positionStart: Int, itemCount: Int) {
        callSuperNotifyItemRangeRemoved(getPositionInAdapter(tag, positionStart), itemCount)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemRangeRemoved].
     *
     * @param section       a visible section of this adapter
     * @param positionStart previous position of the first item that was removed from the section
     * @param itemCount     number of items removed from the section
     */
    fun notifyItemRangeRemovedFromSection(section: Section, positionStart: Int, itemCount: Int) {
        callSuperNotifyItemRangeRemoved(getPositionInAdapter(section, positionStart), itemCount)
    }

    @VisibleForTesting
    internal fun callSuperNotifyItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.notifyItemRangeRemoved(positionStart, itemCount)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemChanged].
     *
     * @param tag      unique identifier of the section
     * @param position position of the item in the section
     */
    fun notifyItemChangedInSection(tag: String, position: Int) {
        callSuperNotifyItemChanged(getPositionInAdapter(tag, position))
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemChanged].
     *
     * @param section  a visible section of this adapter
     * @param position position of the item in the section
     */
    fun notifyItemChangedInSection(section: Section, position: Int) {
        callSuperNotifyItemChanged(getPositionInAdapter(section, position))
    }

    /**
     * Helper method that calculates the relative header position in the adapter and calls
     * [.notifyItemChanged].
     *
     * @param tag unique identifier of the section
     */
    fun notifyHeaderChangedInSection(tag: String) {
        notifyHeaderChangedInSection(getValidSectionOrThrowException(tag))
    }

    /**
     * Helper method that calculates the relative header position in the adapter and calls
     * [.notifyItemChanged].
     *
     * @param section a visible section of this adapter
     */
    fun notifyHeaderChangedInSection(section: Section) {
        callSuperNotifyItemChanged(getHeaderPositionInAdapter(section))
    }

    /**
     * Helper method that calculates the relative footer position in the adapter and calls
     * [.notifyItemChanged].
     *
     * @param tag unique identifier of the section
     */
    fun notifyFooterChangedInSection(tag: String) {
        notifyFooterChangedInSection(getValidSectionOrThrowException(tag))
    }

    /**
     * Helper method that calculates the relative footer position in the adapter and calls
     * [.notifyItemChanged].
     *
     * @param section a visible section of this adapter
     */
    fun notifyFooterChangedInSection(section: Section) {
        callSuperNotifyItemChanged(getFooterPositionInAdapter(section))
    }

    @VisibleForTesting
    internal fun callSuperNotifyItemChanged(position: Int) {
        super.notifyItemChanged(position)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemRangeChanged].
     *
     * @param tag           unique identifier of the section
     * @param positionStart position of the first item that was changed in the section
     * @param itemCount     number of items changed in the section
     */
    fun notifyItemRangeChangedInSection(tag: String, positionStart: Int, itemCount: Int) {
        callSuperNotifyItemRangeChanged(getPositionInAdapter(tag, positionStart), itemCount)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemRangeChanged].
     *
     * @param section       a visible section of this adapter
     * @param positionStart position of the first item that was changed in the section
     * @param itemCount     number of items changed in the section
     */
    fun notifyItemRangeChangedInSection(section: Section, positionStart: Int, itemCount: Int) {
        callSuperNotifyItemRangeChanged(getPositionInAdapter(section, positionStart), itemCount)
    }

    @VisibleForTesting
    internal fun callSuperNotifyItemRangeChanged(positionStart: Int, itemCount: Int) {
        super.notifyItemRangeChanged(positionStart, itemCount)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemRangeChanged].
     *
     * @param tag           unique identifier of the section
     * @param positionStart position of the first item that was inserted in the section
     * @param itemCount     number of items inserted in the section
     * @param payload       optional parameter, use null to identify a "full" update
     */
    fun notifyItemRangeChangedInSection(tag: String, positionStart: Int, itemCount: Int,
                                        payload: Any) {
        callSuperNotifyItemRangeChanged(getPositionInAdapter(tag, positionStart), itemCount, payload)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemRangeChanged].
     *
     * @param section       a visible section of this adapter
     * @param positionStart position of the first item that was inserted in the section
     * @param itemCount     number of items inserted in the section
     * @param payload       optional parameter, use null to identify a "full" update
     */
    fun notifyItemRangeChangedInSection(section: Section, positionStart: Int, itemCount: Int,
                                        payload: Any) {
        callSuperNotifyItemRangeChanged(getPositionInAdapter(section, positionStart), itemCount, payload)
    }

    @VisibleForTesting
    internal fun callSuperNotifyItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any) {
        super.notifyItemRangeChanged(positionStart, itemCount, payload)
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemMoved].
     *
     * @param tag          unique identifier of the section
     * @param fromPosition previous position of the item in the section
     * @param toPosition   new position of the item in the section
     */
    fun notifyItemMovedInSection(tag: String, fromPosition: Int, toPosition: Int) {
        callSuperNotifyItemMoved(getPositionInAdapter(tag, fromPosition), getPositionInAdapter(tag, toPosition))
    }

    /**
     * Helper method that receives position in relation to the section, calculates the relative
     * position in the adapter and calls [.notifyItemMoved].
     *
     * @param section      a visible section of this adapter
     * @param fromPosition previous position of the item in the section
     * @param toPosition   new position of the item in the section
     */
    fun notifyItemMovedInSection(section: Section, fromPosition: Int, toPosition: Int) {
        callSuperNotifyItemMoved(getPositionInAdapter(section, fromPosition), getPositionInAdapter(section, toPosition))
    }

    @VisibleForTesting
    internal fun callSuperNotifyItemMoved(fromPosition: Int, toPosition: Int) {
        super.notifyItemMoved(fromPosition, toPosition)
    }

    /**
     * Helper method that calls [.notifyItemChanged] with the position of the [State]
     * view holder in the adapter. Useful to be called after changing the State from
     * LOADING/FAILED/EMPTY to LOADING/FAILED/EMPTY.
     *
     * @param tag           unique identifier of the section
     * @param previousState previous state of section
     */
    fun notifyNotLoadedStateChanged(tag: String, previousState: State) {
        val section = getValidSectionOrThrowException(tag)

        notifyNotLoadedStateChanged(section, previousState)
    }

    /**
     * Helper method that calls [.notifyItemChanged] with the position of the [State]
     * view holder in the adapter. Useful to be called after changing the State from
     * LOADING/ FAILED/ EMPTY to LOADING/ FAILED/ EMPTY.
     *
     * @param section       a visible section of this adapter
     * @param previousState previous state of section
     */
    fun notifyNotLoadedStateChanged(section: Section, previousState: State) {
        val state = section.state

        if (state == previousState) {
            throw IllegalStateException("No state changed")
        }

        if (previousState == State.LOADED) {
            throw IllegalStateException("Use notifyStateChangedFromLoaded")
        }

        if (state == State.LOADED) {
            throw IllegalStateException("Use notifyStateChangedToLoaded")
        }

        notifyItemChangedInSection(section, 0)
    }

    /**
     * Helper method that calls [.notifyItemChanged] and [.notifyItemInserted] with
     * the position of the [State] view holder in the adapter. Useful to be called after
     * changing the State from LOADING/ FAILED/ EMPTY to LOADED.
     *
     * @param tag           unique identifier of the section
     * @param previousState previous state of section
     */
    fun notifyStateChangedToLoaded(tag: String, previousState: State) {
        val section = getValidSectionOrThrowException(tag)

        notifyStateChangedToLoaded(section, previousState)
    }

    /**
     * Helper method that calls [.notifyItemChanged] and [.notifyItemInserted] with
     * the position of the [State] view holder in the adapter. Useful to be called after
     * changing the State from LOADING/ FAILED/ EMPTY to LOADED.
     *
     * @param section       a visible section of this adapter
     * @param previousState previous state of section
     */
    fun notifyStateChangedToLoaded(section: Section, previousState: State) {
        val state = section.state

        if (state == previousState) {
            throw IllegalStateException("No state changed")
        }

        if (state != State.LOADED) {
            if (previousState == State.LOADED) {
                throw IllegalStateException("Use notifyStateChangedFromLoaded")
            } else {
                throw IllegalStateException("Use notifyNotLoadedStateChanged")
            }
        }

        val contentItemsTotal = section.contentItemsTotal

        if (contentItemsTotal == 0) {
            notifyItemRemovedFromSection(section, 0)
        } else {
            notifyItemChangedInSection(section, 0)

            if (contentItemsTotal > 1) {
                notifyItemRangeInsertedInSection(section, 1, contentItemsTotal - 1)
            }
        }
    }

    /**
     * Helper method that calls [.notifyItemRangeRemoved] and [.notifyItemChanged] with
     * the position of the [State] view holder in the adapter. Useful to be called after
     * changing the State from LOADED to LOADING/ FAILED/ EMPTY.
     *
     * @param tag                       unique identifier of the section
     * @param previousContentItemsCount previous content items count of section
     */
    fun notifyStateChangedFromLoaded(tag: String, previousContentItemsCount: Int) {
        val section = getValidSectionOrThrowException(tag)

        notifyStateChangedFromLoaded(section, previousContentItemsCount)
    }

    /**
     * Helper method that calls [.notifyItemRangeRemoved] and [.notifyItemChanged] with
     * the position of the [State] view holder in the adapter. Useful to be called after
     * changing the State from LOADED to LOADING/ FAILED/ EMPTY.
     *
     * @param section                   a visible section of this adapter
     * @param previousContentItemsCount previous content items count of section
     */
    fun notifyStateChangedFromLoaded(section: Section, previousContentItemsCount: Int) {
        val state = section.state

        if (state == State.LOADED) {
            throw IllegalStateException("Use notifyStateChangedToLoaded")
        }

        if (previousContentItemsCount == 0) {
            notifyItemInsertedInSection(section, 0)
        } else {
            if (previousContentItemsCount > 1) {
                notifyItemRangeRemovedFromSection(section, 1, previousContentItemsCount - 1)
            }

            notifyItemChangedInSection(section, 0)
        }
    }

    /**
     * Helper method that calls [.notifyItemInserted] with the position of the section's
     * header in the adapter. Useful to be called after changing the visibility of the section's
     * header to visible with [Section.setHasHeader].
     *
     * @param tag unique identifier of the section
     */
    fun notifyHeaderInsertedInSection(tag: String) {
        val section = getValidSectionOrThrowException(tag)

        notifyHeaderInsertedInSection(section)
    }

    /**
     * Helper method that calls [.notifyItemInserted] with the position of the section's
     * header in the adapter. Useful to be called after changing the visibility of the section's
     * header to visible with [Section.setHasHeader].
     *
     * @param section a visible section of this adapter
     */
    fun notifyHeaderInsertedInSection(section: Section) {
        val headerPosition = getHeaderPositionInAdapter(section)

        callSuperNotifyItemInserted(headerPosition)
    }

    /**
     * Helper method that calls [.notifyItemInserted] with the position of the section's
     * footer in the adapter. Useful to be called after changing the visibility of the section's
     * footer to visible with [Section.setHasFooter].
     *
     * @param tag unique identifier of the section
     */
    fun notifyFooterInsertedInSection(tag: String) {
        val section = getValidSectionOrThrowException(tag)

        notifyFooterInsertedInSection(section)
    }

    /**
     * Helper method that calls [.notifyItemInserted] with the position of the section's
     * footer in the adapter. Useful to be called after changing the visibility of the section's
     * footer to visible with [Section.setHasFooter].
     *
     * @param section a visible section of this adapter
     */
    fun notifyFooterInsertedInSection(section: Section) {
        val footerPosition = getFooterPositionInAdapter(section)

        callSuperNotifyItemInserted(footerPosition)
    }

    /**
     * Helper method that calls [.notifyItemRemoved] with the position of the section's
     * header in the adapter. Useful to be called after changing the visibility of the section's
     * header to invisible with [Section.setHasHeader].
     *
     * @param tag unique identifier of the section
     */
    fun notifyHeaderRemovedFromSection(tag: String) {
        val section = getValidSectionOrThrowException(tag)

        notifyHeaderRemovedFromSection(section)
    }

    /**
     * Helper method that calls [.notifyItemRemoved] with the position of the section's
     * header in the adapter. Useful to be called after changing the visibility of the section's
     * header to invisible with [Section.setHasHeader].
     *
     * @param section a visible section of this adapter
     */
    fun notifyHeaderRemovedFromSection(section: Section) {
        val position = getSectionPosition(section)

        callSuperNotifyItemRemoved(position)
    }

    /**
     * Helper method that calls [.notifyItemRemoved] with the position of the section's
     * footer in the adapter. Useful to be called after changing the visibility of the section's
     * footer to invisible with [Section.setHasFooter].
     *
     * @param tag unique identifier of the section
     */
    fun notifyFooterRemovedFromSection(tag: String) {
        val section = getValidSectionOrThrowException(tag)

        notifyFooterRemovedFromSection(section)
    }

    /**
     * Helper method that calls [.notifyItemRemoved] with the position of the section's
     * footer in the adapter. Useful to be called after changing the visibility of the section's
     * footer to invisible with [Section.setHasFooter].
     *
     * @param section a visible section of this adapter
     */
    fun notifyFooterRemovedFromSection(section: Section) {
        val position = getSectionPosition(section) + section.sectionItemsTotal

        callSuperNotifyItemRemoved(position)
    }

    /**
     * Helper method that calls [.notifyItemRangeInserted] with the position of the section
     * in the adapter. Useful to be called after changing the visibility of the section to visible
     * with [Section.setVisible].
     *
     * @param tag unique identifier of the section
     */
    fun notifySectionChangedToVisible(tag: String) {
        val section = getValidSectionOrThrowException(tag)

        notifySectionChangedToVisible(section)
    }

    /**
     * Helper method that calls [.notifyItemRangeInserted] with the position of the section
     * in the adapter. Useful to be called after changing the visibility of the section to visible
     * with [Section.setVisible].
     *
     * @param section a visible section of this adapter
     */
    fun notifySectionChangedToVisible(section: Section) {
        if (!section.isVisible) {
            throw IllegalStateException("This section is not visible.")
        }

        val sectionPosition = getSectionPosition(section)

        val sectionItemsTotal = section.sectionItemsTotal

        callSuperNotifyItemRangeInserted(sectionPosition, sectionItemsTotal)
    }

    /**
     * Helper method that calls [.notifyItemRangeInserted] with the position of the section
     * in the adapter. Useful to be called after changing the visibility of the section to invisible
     * with [Section.setVisible].
     *
     * @param tag                     unique identifier of the section
     * @param previousSectionPosition previous section position
     */
    fun notifySectionChangedToInvisible(tag: String, previousSectionPosition: Int) {
        val section = getValidSectionOrThrowException(tag)

        notifySectionChangedToInvisible(section, previousSectionPosition)
    }

    /**
     * Helper method that calls [.notifyItemRangeInserted] with the position of the section
     * in the adapter. Useful to be called after changing the visibility of the section to invisible
     * with [Section.setVisible].
     *
     * @param section                 an invisible section of this adapter
     * @param previousSectionPosition previous section position
     */
    fun notifySectionChangedToInvisible(section: Section, previousSectionPosition: Int) {
        if (section.isVisible) {
            throw IllegalStateException("This section is not visible.")
        }

        val sectionItemsTotal = section.sectionItemsTotal

        callSuperNotifyItemRangeRemoved(previousSectionPosition, sectionItemsTotal)
    }

    private fun getValidSectionOrThrowException(tag: String): Section {

        return getSection(tag) ?: throw IllegalArgumentException("Invalid tag: $tag")
    }

    /**
     * A concrete class of an empty ViewHolder.
     * Should be used to avoid the boilerplate of creating a ViewHolder class for simple case
     * scenarios.
     */
    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {

        val VIEW_TYPE_HEADER = 0
        val VIEW_TYPE_FOOTER = 1
        val VIEW_TYPE_ITEM_LOADED = 2
        val VIEW_TYPE_LOADING = 3
        val VIEW_TYPE_FAILED = 4
        val VIEW_TYPE_EMPTY = 5
        private val VIEW_TYPE_QTY = 6
    }
}
