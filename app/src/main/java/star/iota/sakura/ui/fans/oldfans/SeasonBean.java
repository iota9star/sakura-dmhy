package star.iota.sakura.ui.fans.oldfans;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import star.iota.sakura.ui.fans.FanBean;

class SeasonBean {
    @SerializedName("Sun")
    private List<FanBean> Sun;
    @SerializedName("Mon")
    private List<FanBean> Mon;
    @SerializedName("Tue")
    private List<FanBean> Tue;
    @SerializedName("Wed")
    private List<FanBean> Wed;
    @SerializedName("Thu")
    private List<FanBean> Thu;
    @SerializedName("Fri")
    private List<FanBean> Fri;
    @SerializedName("Sat")
    private List<FanBean> Sat;

    List<FanBean> getSun() {
        return Sun;
    }


    List<FanBean> getMon() {
        return Mon;
    }


    List<FanBean> getTue() {
        return Tue;
    }


    List<FanBean> getWed() {
        return Wed;
    }


    List<FanBean> getThu() {
        return Thu;
    }


    List<FanBean> getFri() {
        return Fri;
    }


    List<FanBean> getSat() {
        return Sat;
    }

}
