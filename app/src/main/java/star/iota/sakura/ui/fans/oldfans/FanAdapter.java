package star.iota.sakura.ui.fans.oldfans;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import star.iota.sakura.R;
import star.iota.sakura.base.BaseAdapter;
import star.iota.sakura.ui.fans.bean.FanBean;


public class FanAdapter extends BaseAdapter<FanViewHolder, FanBean> {

    @Override
    public FanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FanViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fan, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FanViewHolder) holder).bindView(mBeans.get(position));
    }
}
