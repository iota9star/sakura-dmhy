package star.iota.sakura.ui.fans.newfans;


import java.util.List;

import star.iota.sakura.ui.fans.FanBean;


class NewFansBean {
    private String week;
    private List<FanBean> fans;

    String getWeek() {
        return week;
    }

    void setWeek(String week) {
        this.week = week;
    }

    public List<FanBean> getFans() {
        return fans;
    }

    public void setFans(List<FanBean> fans) {
        this.fans = fans;
    }
}
