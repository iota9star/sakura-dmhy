package star.iota.sakura.ui.fans.oldfans;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import star.iota.sakura.ui.fans.bean.FanBean;

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

    public List<FanBean> getSun() {
        return Sun;
    }


    public List<FanBean> getMon() {
        return Mon;
    }


    public List<FanBean> getTue() {
        return Tue;
    }


    public List<FanBean> getWed() {
        return Wed;
    }


    public List<FanBean> getThu() {
        return Thu;
    }


    public List<FanBean> getFri() {
        return Fri;
    }


    public List<FanBean> getSat() {
        return Sat;
    }

}
