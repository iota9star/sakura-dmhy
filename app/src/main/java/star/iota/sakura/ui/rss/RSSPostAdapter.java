package star.iota.sakura.ui.rss;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import star.iota.sakura.R;
import star.iota.sakura.base.BaseAdapter;
import star.iota.sakura.base.BaseViewHolder;

class RSSPostAdapter extends BaseAdapter<BaseViewHolder, RSSPostBean> {

    private static final int WITH_COVER = 1;
    private static final int NO_COVER = 2;

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == WITH_COVER) {
            return new WithCoverViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rss_post_with_cover, parent, false));
        } else {
            return new NoCoverViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rss_post_no_cover, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mBeans.get(position).getCover() == null) {
            ((NoCoverViewHolder) holder).bindView(mBeans.get(position));
        } else {
            ((WithCoverViewHolder) holder).bindView(mBeans.get(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mBeans.get(position).getCover() != null ? WITH_COVER : NO_COVER;
    }
}
