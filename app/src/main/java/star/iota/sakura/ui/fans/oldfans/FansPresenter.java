package star.iota.sakura.ui.fans.oldfans;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import star.iota.sakura.WeekIcon;
import star.iota.sakura.base.PVContract;
import star.iota.sakura.base.StringPresenter;


class FansPresenter extends StringPresenter<List<FansBean>> {
    FansPresenter(PVContract.View view) {
        super(view);
    }

    @Override
    protected List<FansBean> dealResponse(String s) {
        SeasonBean result = new Gson().fromJson(s, SeasonBean.class);
        List<FansBean> fans = new ArrayList<>();
        if (result.getSun() != null && result.getSun().size() > 0) {
            FansBean bean = new FansBean();
            bean.setWeek(WeekIcon.SUN);
            bean.setFans(result.getSun());
            fans.add(bean);
        }
        if (result.getMon() != null && result.getMon().size() > 0) {
            FansBean bean = new FansBean();
            bean.setWeek(WeekIcon.MON);
            bean.setFans(result.getMon());
            fans.add(bean);
        }
        if (result.getTue() != null && result.getTue().size() > 0) {
            FansBean bean = new FansBean();
            bean.setWeek(WeekIcon.TUE);
            bean.setFans(result.getTue());
            fans.add(bean);
        }
        if (result.getWed() != null && result.getWed().size() > 0) {
            FansBean bean = new FansBean();
            bean.setWeek(WeekIcon.WED);
            bean.setFans(result.getWed());
            fans.add(bean);
        }
        if (result.getThu() != null && result.getThu().size() > 0) {
            FansBean bean = new FansBean();
            bean.setWeek(WeekIcon.THU);
            bean.setFans(result.getThu());
            fans.add(bean);
        }
        if (result.getFri() != null && result.getFri().size() > 0) {
            FansBean bean = new FansBean();
            bean.setWeek(WeekIcon.FRI);
            bean.setFans(result.getFri());
            fans.add(bean);
        }
        if (result.getSat() != null && result.getSat().size() > 0) {
            FansBean bean = new FansBean();
            bean.setWeek(WeekIcon.SAT);
            bean.setFans(result.getSat());
            fans.add(bean);
        }
        return fans;
    }
}
