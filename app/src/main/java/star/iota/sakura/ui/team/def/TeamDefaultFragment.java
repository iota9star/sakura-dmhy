package star.iota.sakura.ui.team.def;

import star.iota.sakura.Menus;
import star.iota.sakura.base.BaseAdapter;
import star.iota.sakura.ui.team.TeamFragment;
import star.iota.sakura.utils.MessageBar;


public class TeamDefaultFragment extends TeamFragment {
    @Override
    protected BaseAdapter getAdapter() {
        setToolbarTitle(Menus.TEAM_DEFAULT);
        return new TeamDefaultAdapter();
    }

}
