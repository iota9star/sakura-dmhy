package star.iota.sakura.ui.team.def;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import star.iota.sakura.R;
import star.iota.sakura.base.BaseAdapter;
import star.iota.sakura.ui.team.TeamBean;


class TeamDefaultAdapter extends BaseAdapter<TeamDefaultViewHolder, TeamBean> {

    @Override
    public TeamDefaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TeamDefaultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TeamDefaultViewHolder) holder).bindView(mBeans.get(position));
    }
}
