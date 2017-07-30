package star.iota.sakura.ui.fans.newfans;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import star.iota.sakura.R;
import star.iota.sakura.base.BaseAdapter;


public class NewFansAdapter extends BaseAdapter<NewFansViewHolder, NewFansBean> {

    @Override
    public NewFansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewFansViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fans, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((NewFansViewHolder) holder).bindView(mBeans.get(position));
    }
}
