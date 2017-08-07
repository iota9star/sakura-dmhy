package star.iota.sakura.database;


import java.util.List;

import star.iota.sakura.ui.fans.bean.FanBean;

public interface FanDAO {
    boolean save(FanBean bean);

    boolean delete(FanBean bean);

    boolean deleteAll();

    boolean exist(FanBean bean);

    List<FanBean> query();
}
