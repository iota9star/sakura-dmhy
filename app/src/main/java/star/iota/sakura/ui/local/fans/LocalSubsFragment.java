package star.iota.sakura.ui.local.fans;

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
import star.iota.sakura.database.SubsDAO;
import star.iota.sakura.database.SubsDAOImpl;
import star.iota.sakura.ui.post.PostBean;
import star.iota.sakura.utils.SnackbarUtils;


public class LocalSubsFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private LocalSubsAdapter mAdapter;

    public static LocalSubsFragment newInstance() {
        return new LocalSubsFragment();
    }

    @Override
    protected void init() {
        setTitle("收藏 * 單項");
        mAdapter = new LocalSubsAdapter();
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
        Observable.just(new SubsDAOImpl(mContext))
                .map(new Function<SubsDAO, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull SubsDAO subsDAO) throws Exception {
                        return subsDAO.deleteAll();
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
