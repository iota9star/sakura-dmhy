package star.iota.sakura.ui.fans.oldfans;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import star.iota.sakura.R;
import star.iota.sakura.base.BaseAdapter;


class FansAdapter extends BaseAdapter<FansViewHolder, FansBean> {

    @Override
    public FansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FansViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fans, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FansViewHolder) holder).bindView(mBeans.get(position));
    }
}
