package star.iota.sakura.ui.team.rss;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import star.iota.sakura.R;
import star.iota.sakura.base.BaseAdapter;
import star.iota.sakura.ui.team.TeamBean;


class TeamRSSAdapter extends BaseAdapter<TeamRSSViewHolder, TeamBean> {

    @Override
    public TeamRSSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TeamRSSViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TeamRSSViewHolder) holder).bindView(mBeans.get(position));
    }
}
