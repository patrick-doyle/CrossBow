package com.crossbow.volley.toolbox;

import android.widget.AbsListView;

import com.android.volley.RequestQueue;

/**

 */
public class RelayScrollListener implements AbsListView.OnScrollListener {

    private AbsListView.OnScrollListener onScrollListener;
    private RequestQueue requestQueue;

    public RelayScrollListener(AbsListView.OnScrollListener onScrollListener, RequestQueue requestQueue) {
        this.onScrollListener = onScrollListener;
        this.requestQueue = requestQueue;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_FLING) {
            requestQueue.stop();
        }
        else {
            requestQueue.start();
        }
        if(onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
