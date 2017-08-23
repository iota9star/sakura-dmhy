package star.iota.sakura.ui.team.def;

import star.iota.sakura.Menus;
import star.iota.sakura.base.BaseAdapter;
import star.iota.sakura.ui.team.TeamFragment;


public class TeamDefaultFragment extends TeamFragment {
    @Override
    protected BaseAdapter getAdapter() {
        setTitle(Menus.TEAM_DEFAULT);
        return new TeamDefaultAdapter();
    }
}
