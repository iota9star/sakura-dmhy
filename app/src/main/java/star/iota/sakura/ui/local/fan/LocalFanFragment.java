package star.iota.sakura.ui.local.fan;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseFragment;
import star.iota.sakura.base.SGSpacingItemDecoration;
import star.iota.sakura.database.FanDAO;
import star.iota.sakura.database.FanDAOImpl;
import star.iota.sakura.ui.fans.bean.FanBean;
import star.iota.sakura.utils.SnackbarUtils;


public class LocalFanFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    public static LocalFanFragment newInstance() {
        return new LocalFanFragment();
    }

    @Override
    protected void init() {
        setTitle("收藏 * 番組");
        final LocalFanAdapter adapter = new LocalFanAdapter();
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.addItemDecoration(new SGSpacingItemDecoration(1, mContext.getResources().getDimensionPixelOffset(R.dimen.v4dp)));
        mRecyclerView.setAdapter(adapter);
        Observable.just(new FanDAOImpl(mContext))
                .map(new Function<FanDAO, List<FanBean>>() {
                    @Override
                    public List<FanBean> apply(@NonNull FanDAO fanDao) throws Exception {
                        return fanDao.query();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FanBean>>() {
                    @Override
                    public void accept(final List<FanBean> beans) throws Exception {
                        Collections.reverse(beans);
                        mRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.add(beans);
                            }
                        }, 480);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        SnackbarUtils.create(mContext, "可能出現錯誤：" + throwable.getMessage());
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }


}
