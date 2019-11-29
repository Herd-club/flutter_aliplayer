package com.zxq.flutter_aliplayer.view.sectionlist

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Abstract [Section] with no states.
 */
abstract class StatelessSection
/**
 * Create a stateless Section object based on [SectionParameters].
 *
 * @param sectionParameters section parameters
 */
(sectionParameters: SectionParameters) : Section(sectionParameters) {

    init {

        if (sectionParameters.loadingResourceId != null) {
            throw IllegalArgumentException("Stateless section shouldn't have a loading state resource")
        }

        if (sectionParameters.loadingViewWillBeProvided) {
            throw IllegalArgumentException("Stateless section shouldn't have loadingViewWillBeProvided set")
        }

        if (sectionParameters.failedResourceId != null) {
            throw IllegalArgumentException("Stateless section shouldn't have a failed state resource")
        }

        if (sectionParameters.failedViewWillBeProvided) {
            throw IllegalArgumentException("Stateless section shouldn't have failedViewWillBeProvided set")
        }

        if (sectionParameters.emptyResourceId != null) {
            throw IllegalArgumentException("Stateless section shouldn't have an empty state resource")
        }

        if (sectionParameters.emptyViewWillBeProvided) {
            throw IllegalArgumentException("Stateless section shouldn't have emptyViewWillBeProvided set")
        }
    }

    // Override these methods to make them final.

    override fun onBindLoadingViewHolder(holder: RecyclerView.ViewHolder) {
        super.onBindLoadingViewHolder(holder)
    }

    override fun getLoadingViewHolder(view: View): RecyclerView.ViewHolder {
        return super.getLoadingViewHolder(view)
    }

    override fun onBindFailedViewHolder(holder: RecyclerView.ViewHolder) {
        super.onBindFailedViewHolder(holder)
    }

    override fun getFailedViewHolder(view: View): RecyclerView.ViewHolder {
        return super.getFailedViewHolder(view)
    }

    override fun onBindEmptyViewHolder(holder: RecyclerView.ViewHolder) {
        super.onBindEmptyViewHolder(holder)
    }

    override fun getEmptyViewHolder(view: View): RecyclerView.ViewHolder {
        return super.getEmptyViewHolder(view)
    }
}
