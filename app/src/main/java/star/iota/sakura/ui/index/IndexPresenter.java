package star.iota.sakura.ui.index;

import com.google.gson.Gson;

import star.iota.sakura.base.PVContract;
import star.iota.sakura.base.StringPresenter;


class IndexPresenter extends StringPresenter<IndexBean> {

    IndexPresenter(PVContract.View view) {
        super(view);
    }

    @Override
    protected IndexBean dealResponse(String s) {
        return new Gson().fromJson(s, IndexBean.class);
    }
}
