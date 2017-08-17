package star.iota.sakura.database;


import java.util.List;

import star.iota.sakura.ui.fans.FanBean;

public interface FanDAO {
    boolean save(FanBean bean);

    boolean save(List<FanBean> beans);

    boolean delete(FanBean bean);

    boolean deleteAll();

    boolean exist(FanBean bean);

    List<FanBean> query();
}
