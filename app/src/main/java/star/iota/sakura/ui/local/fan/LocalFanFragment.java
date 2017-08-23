package star.iota.sakura.ui.local.fan;

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
import star.iota.sakura.database.FanDAO;
import star.iota.sakura.database.FanDAOImpl;
import star.iota.sakura.ui.fans.FanBean;
import star.iota.sakura.ui.main.MainActivity;
import star.iota.sakura.utils.FileUtils;
import star.iota.sakura.utils.MessageBar;


public class LocalFanFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private LocalFanAdapter mAdapter;
    private boolean isRunning;

    public static LocalFanFragment newInstance() {
        return new LocalFanFragment();
    }

    @Override
    protected void init() {
        setTitle("收藏 * 番組");
        mAdapter = new LocalFanAdapter();
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
                    MessageBar.create(mContext, "正在加載中...");
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
                        FileUtils.showFileChooser(getActivity(), MainActivity.FAN_IMPORT_CODE);
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
                                .setPositiveButton("摁錯了", new DialogInterface.OnClickListener() {
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
        final String backupPath = FileUtils.getBackupPath();
        if (backupPath == null) {
            MessageBar.create(mContext, "您可能沒有掛載SD卡");
            return;
        }
        final String rawBackupPath = backupPath + "番組/";
        final String backupFileName = FileUtils.getBackupFileName();
        Observable.just(new FanDAOImpl(mContext))
                .map(new Function<FanDAO, List<FanBean>>() {
                    @Override
                    public List<FanBean> apply(@NonNull FanDAO fanDAO) throws Exception {
                        return fanDAO.query();
                    }
                })
                .map(new Function<List<FanBean>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull List<FanBean> fanBeen) throws Exception {
                        return FileUtils.backup(rawBackupPath, backupFileName, new Gson().toJson(fanBeen));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            MessageBar.create(mContext, "備份成功：" + rawBackupPath + backupFileName);
                        } else {
                            MessageBar.create(mContext, "由於未知原因備份失敗");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        MessageBar.create(mContext, "備份失敗：" + throwable.getMessage());
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
                            MessageBar.create(mContext, "已清空收藏");
                        } else {
                            MessageBar.create(mContext, "可能出現錯誤，請重試");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        MessageBar.create(mContext, "可能出現錯誤：" + throwable.getMessage());
                    }
                });
    }

    private void loadData() {
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
                        mRefreshLayout.finishRefresh();
                        Collections.reverse(beans);
                        mAdapter.add(beans);
                        isRunning = false;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mRefreshLayout.finishRefresh();
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
