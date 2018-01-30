package star.iota.sakura.ui.local.subs;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.github.rubensousa.floatingtoolbar.FloatingToolbar;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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
import star.iota.sakura.database.SubsDAO;
import star.iota.sakura.database.SubsDAOImpl;
import star.iota.sakura.ui.main.MainActivity;
import star.iota.sakura.ui.post.PostBean;
import star.iota.sakura.utils.FileUtils;
import star.iota.sakura.utils.MessageBar;


public class LocalSubsFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private LocalSubsAdapter mAdapter;
    private boolean isRunning;

    public static LocalSubsFragment newInstance() {
        return new LocalSubsFragment();
    }

    @Override
    protected void init() {
        setToolbarTitle("收藏 * 單項");
        mAdapter = new LocalSubsAdapter();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        initRefreshLayout();
        initFab();
    }

    private void initRefreshLayout() {
        isRunning = false;
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (isRunning) {
                    MessageBar.create(mContext, "正在加载中...");
                    return;
                }
                isRunning = true;
                mAdapter.clear();
                loadData();
            }
        });
    }

    private void initFab() {
        FloatingActionButton fab = getFab();
        FloatingToolbar floatingToolbar = getFloatingToolbar();
        floatingToolbar.attachRecyclerView(mRecyclerView);
        floatingToolbar.attachFab(fab);
        floatingToolbar.setClickListener(new FloatingToolbar.ItemClickListener() {
            @Override
            public void onItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_export:
                        exportBackup();
                        break;
                    case R.id.action_import:
                        FileUtils.showFileChooser(getActivity(), MainActivity.SUBS_IMPORT_CODE);
                        break;
                    case R.id.action_clear:
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
                        break;
                }
            }

            @Override
            public void onItemLongClick(MenuItem menuItem) {

            }
        });
    }

    private void exportBackup() {
        String backupPath = FileUtils.getBackupPath();
        if (backupPath == null) {
            MessageBar.create(mContext, "您可能没有挂载SD卡");
            return;
        }
        final String rawBackupPath = backupPath + "單項/";
        final String backupFileName = FileUtils.getBackupFileName();
        Observable.just(new SubsDAOImpl(mContext))
                .map(new Function<SubsDAO, List<PostBean>>() {
                    @Override
                    public List<PostBean> apply(@NonNull SubsDAO subsDAO) throws Exception {
                        return subsDAO.query();
                    }
                })
                .map(new Function<List<PostBean>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull List<PostBean> fanBeen) throws Exception {
                        return FileUtils.backup(rawBackupPath, backupFileName, new Gson().toJson(fanBeen));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            MessageBar.create(mContext, "备份成功：" + rawBackupPath + backupFileName);
                        } else {
                            MessageBar.create(mContext, "由于未知原因备份失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        MessageBar.create(mContext, "备份失败：" + throwable.getMessage());
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
                            MessageBar.create(mContext, "已清空收藏");
                        } else {
                            MessageBar.create(mContext, "可能出现错误，请重试");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        MessageBar.create(mContext, "可能出现错误：" + throwable.getMessage());
                    }
                });
    }

    private void loadData() {
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
                        mRefreshLayout.finishRefresh();
                        Collections.reverse(beans);
                        mAdapter.add(beans);
                        isRunning = false;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isRunning = false;
                        MessageBar.create(mContext, "可能出現錯誤：" + throwable.getMessage());
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_with_refresh_layout;
    }

    @Override
    protected boolean isHideFab() {
        return false;
    }
}
