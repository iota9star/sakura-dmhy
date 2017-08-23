package star.iota.sakura.ui.team.rss;

import star.iota.sakura.Menus;
import star.iota.sakura.base.BaseAdapter;
import star.iota.sakura.ui.team.TeamFragment;


public class TeamRSSFragment extends TeamFragment {
    @Override
    protected BaseAdapter getAdapter() {
        setTitle(Menus.RSS + "*" + Menus.RSS_SUBS);
        return new TeamRSSAdapter();
    }
}
