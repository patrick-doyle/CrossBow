package com.crossbow.volley.toolbox;

import android.widget.AbsListView;

/**
 * Created by Patrick on 12/04/2015.
 */
public class RelayScrollListener implements AbsListView.OnScrollListener {

    private AbsListView.OnScrollListener onScrollListener;
    private Crossbow crossbow;

    public RelayScrollListener(AbsListView.OnScrollListener onScrollListener, Crossbow crossbow) {
        this.onScrollListener = onScrollListener;
        this.crossbow = crossbow;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_FLING) {
            crossbow.stopQueue();
        }
        else {
            crossbow.startQueue();
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
