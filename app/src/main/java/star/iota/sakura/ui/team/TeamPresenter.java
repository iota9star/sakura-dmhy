package star.iota.sakura.ui.team;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import star.iota.sakura.base.PVContract;
import star.iota.sakura.base.StringPresenter;


class TeamPresenter extends StringPresenter<List<TeamBean>> {

    TeamPresenter(PVContract.View view) {
        super(view);
    }

    @Override
    protected List<TeamBean> dealResponse(String s) {
        List<TeamBean> beans = new ArrayList<>();
        Elements elements = Jsoup.parse(s).select("#AdvSearchTeam > option");
        for (Element ele : elements) {
            String name = ele.text();
            if (name.contains("全部")) continue;
            String id = ele.attr("value");
            beans.add(new TeamBean(id, name));
        }
        return beans;
    }
}
