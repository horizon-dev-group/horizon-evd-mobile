package com.example.fibath.ui.transaction;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

public abstract class PaginationScrollListenerForNestedScrollView implements NestedScrollView.OnScrollChangeListener {
    LinearLayoutManager layoutManager;
    public PaginationScrollListenerForNestedScrollView(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if(v.getChildAt(v.getChildCount() - 1) != null) {
            if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                    scrollY > oldScrollY) {

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();



//                if (!isLoading() && !isLastPage()) {
//                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount
//                            && pastVisiblesItems >= 0
//                            && totalItemCount >= getTotalPageCount()) {
//                        loadMoreItems();
//                    }
//                }

                if (!isLoading() && !isLastPage()) {

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
loadMoreItems();
//  Load Your Data
                    }
                }
            }
        }
    }
    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}
