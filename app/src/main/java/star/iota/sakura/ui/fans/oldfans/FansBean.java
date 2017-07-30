package star.iota.sakura.ui.fans.oldfans;


import java.util.List;

import star.iota.sakura.ui.fans.bean.FanBean;

class FansBean {
    private String week;
    private List<FanBean> fans;

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public List<FanBean> getFans() {
        return fans;
    }

    public void setFans(List<FanBean> fans) {
        this.fans = fans;
    }
}
