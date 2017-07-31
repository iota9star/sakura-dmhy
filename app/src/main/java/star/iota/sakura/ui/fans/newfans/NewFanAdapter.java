package star.iota.sakura.ui.fans.newfans;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import star.iota.sakura.R;
import star.iota.sakura.base.BaseAdapter;
import star.iota.sakura.ui.fans.bean.FanBean;


class NewFanAdapter extends BaseAdapter<NewFanViewHolder, FanBean> {

    @Override
    public NewFanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewFanViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fan, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((NewFanViewHolder) holder).bindView(mBeans.get(position));
    }
}
