package star.iota.sakura.base;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

public class SGSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int spacing;
    private int spanCount;

    public SGSpacingItemDecoration(int spanCount, int spacing) {
        this.spanCount = spanCount;
        this.spacing = spacing;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
        if (spanCount == 1) {
            outRect.left = spacing;
            outRect.right = spacing;
        } else if (spanIndex == 0) {
            outRect.left = spacing;
            outRect.right = spacing / 2;
        } else if (spanIndex == (spanCount - 1)) {
            outRect.left = spacing / 2;
            outRect.right = spacing;
        } else {
            outRect.left = spacing / 2;
            outRect.right = spacing / 2;
        }
        outRect.top = spacing;
    }
}
