package star.iota.sakura.ui.local.fan;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

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
    private LocalFanAdapter mAdapter;

    public static LocalFanFragment newInstance() {
        return new LocalFanFragment();
    }

    @Override
    protected void init() {
        setTitle("收藏 * 番組");
        mAdapter = new LocalFanAdapter();
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.addItemDecoration(new SGSpacingItemDecoration(1, mContext.getResources().getDimensionPixelOffset(R.dimen.v4dp)));
        mRecyclerView.setAdapter(mAdapter);
        initData();
        initFab();
    }

    private void initFab() {
        FloatingActionButton fab = getFab();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setIcon(R.mipmap.app_icon)
                        .setTitle("清空列表")
                        .setNegativeButton("嗯", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                clearCollection();
                            }
                        })
                        .setPositiveButton("按错了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void clearCollection() {
        Observable.just(new FanDAOImpl(mContext))
                .map(new Function<FanDAO, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull FanDAO fanDAO) throws Exception {
                        return fanDAO.deleteAll();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            mAdapter.clear();
                            mRecyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "已清空收藏", Toast.LENGTH_SHORT).show();
                                }
                            }, 640);
                        } else {
                            Toast.makeText(mContext, "可能出现错误，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(mContext, "可能出现错误：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initData() {
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
                                mAdapter.add(beans);
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

    @Override
    protected boolean isHideFab() {
        return false;
    }
}
