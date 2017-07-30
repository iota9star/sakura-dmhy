package star.iota.sakura.ui.fans.newfans;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import star.iota.sakura.Url;
import star.iota.sakura.WeekIcon;
import star.iota.sakura.base.PVContract;
import star.iota.sakura.base.StringPresenter;
import star.iota.sakura.ui.fans.bean.FanBean;
import star.iota.sakura.ui.fans.bean.SubBean;


public class NewFansPresenter extends StringPresenter<List<NewFansBean>> {

    NewFansPresenter(PVContract.View view) {
        super(view);
    }

    @Override
    protected List<NewFansBean> dealResponse(String s) {
        String regex = "[a-z]{3}array\\.push\\(\\[(.*)\\]\\);";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(s);
        List<NewFansBean> list = new ArrayList<>();
        NewFansBean fansSun = new NewFansBean();
        NewFansBean fansMon = new NewFansBean();
        NewFansBean fansTue = new NewFansBean();
        NewFansBean fansWed = new NewFansBean();
        NewFansBean fansThu = new NewFansBean();
        NewFansBean fansFri = new NewFansBean();
        NewFansBean fansSat = new NewFansBean();
        List<FanBean> Sun = new ArrayList<>();
        List<FanBean> Mon = new ArrayList<>();
        List<FanBean> Tue = new ArrayList<>();
        List<FanBean> Wed = new ArrayList<>();
        List<FanBean> Thu = new ArrayList<>();
        List<FanBean> Fri = new ArrayList<>();
        List<FanBean> Sat = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group();
            String[] arr = matcher.group(1).replaceAll("'", "").split(",");
            if (arr.length != 5) return list;
            FanBean fan = new FanBean();
            fan.setCover(arr[0]);
            fan.setName(arr[1]);
            fan.setKeyword(arr[2]);
            List<SubBean> subs = new ArrayList<>();
            Elements elements = Jsoup.parseBodyFragment(arr[3]).select("a");
            for (Element ele : elements) {
                SubBean sub = new SubBean();
                sub.setName(ele.text());
                sub.setUrl(Url.BASE + ele.attr("href"));
                subs.add(sub);
            }
            fan.setSubs(subs);
            fan.setOfficial(arr[4]);
            if (group.startsWith("sun")) {
                Sun.add(fan);
                fansSun.setWeek(WeekIcon.SUN);
            } else if (group.startsWith("mon")) {
                Mon.add(fan);
                fansMon.setWeek(WeekIcon.MON);
            } else if (group.startsWith("tue")) {
                Tue.add(fan);
                fansTue.setWeek(WeekIcon.TUE);
            } else if (group.startsWith("wed")) {
                Wed.add(fan);
                fansWed.setWeek(WeekIcon.WED);
            } else if (group.startsWith("thu")) {
                Thu.add(fan);
                fansThu.setWeek(WeekIcon.THU);
            } else if (group.startsWith("fri")) {
                Fri.add(fan);
                fansFri.setWeek(WeekIcon.FRI);
            } else if (group.startsWith("sat")) {
                Sat.add(fan);
                fansSat.setWeek(WeekIcon.SAT);
            }
        }
        if (Sun.size() > 0) {
            fansSun.setFans(Sun);
            list.add(fansSun);
        }
        if (Mon.size() > 0) {
            fansMon.setFans(Mon);
            list.add(fansMon);
        }
        if (Tue.size() > 0) {
            fansTue.setFans(Tue);
            list.add(fansTue);
        }
        if (Wed.size() > 0) {
            fansWed.setFans(Wed);
            list.add(fansWed);
        }
        if (Thu.size() > 0) {
            fansThu.setFans(Thu);
            list.add(fansThu);
        }
        if (Fri.size() > 0) {
            fansFri.setFans(Fri);
            list.add(fansFri);
        }
        if (Sat.size() > 0) {
            fansSat.setFans(Sat);
            list.add(fansSat);
        }
        return list;
    }
}
