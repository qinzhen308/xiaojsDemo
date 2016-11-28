package com.benyuan.xiaojs.common.pulltorefresh.stickylistheaders;

/**
 * Listener to get callback notifications for the StickyListView
 */
public interface StickyListViewListener {

	void onNextPageShown(boolean shown);

	/**
	 * User is in first item of list
	 */
	void onFirstListItem();

	/**
	 * User is in last item of list
	 */
	void onLastListItem();

}
