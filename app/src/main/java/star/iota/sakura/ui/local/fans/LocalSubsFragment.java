package star.iota.sakura.ui.local.fans;

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
import star.iota.sakura.database.SubsDAO;
import star.iota.sakura.database.SubsDAOImpl;
import star.iota.sakura.ui.post.PostBean;
import star.iota.sakura.utils.SnackbarUtils;


public class LocalSubsFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    public static LocalSubsFragment newInstance() {
        return new LocalSubsFragment();
    }

    @Override
    protected void init() {
        setTitle("收藏 * 單項");
        final LocalSubsAdapter adapter = new LocalSubsAdapter();
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.addItemDecoration(new SGSpacingItemDecoration(1, mContext.getResources().getDimensionPixelOffset(R.dimen.v4dp)));
        mRecyclerView.setAdapter(adapter);
        Observable.just(new SubsDAOImpl(mContext))
                .map(new Function<SubsDAO, List<PostBean>>() {
                    @Override
                    public List<PostBean> apply(@NonNull SubsDAO subsDAO) throws Exception {
                        return subsDAO.query();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<PostBean>>() {
                    @Override
                    public void accept(final List<PostBean> beans) throws Exception {
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
