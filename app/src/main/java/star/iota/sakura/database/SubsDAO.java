package star.iota.sakura.database;


import java.util.List;

import star.iota.sakura.ui.post.PostBean;

public interface SubsDAO {
    boolean save(PostBean bean);

    boolean save(List<PostBean> beans);

    boolean delete(Integer id);

    boolean deleteAll();

    boolean exist(String url);

    List<PostBean> query();
}
