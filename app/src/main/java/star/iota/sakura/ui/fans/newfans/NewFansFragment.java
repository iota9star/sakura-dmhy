package star.iota.sakura.ui.fans.newfans;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import star.iota.sakura.R;
import star.iota.sakura.Url;
import star.iota.sakura.base.BaseFragment;
import star.iota.sakura.base.PVContract;
import star.iota.sakura.base.SGSpacingItemDecoration;
import star.iota.sakura.utils.SnackbarUtils;


public class NewFansFragment extends BaseFragment implements PVContract.View<List<NewFansBean>> {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private NewFansAdapter mAdapter;

    private NewFansPresenter mPresenter;
    private boolean isRunning;

    @Override
    protected void init() {
        setTitle("每週番組");
        initPresenter();
        initRecyclerView();
        initRefreshLayout();
    }

    private void initPresenter() {
        isRunning = false;
        mPresenter = new NewFansPresenter(this);
    }

    private void initRefreshLayout() {
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (isRunning) {
                    SnackbarUtils.create(mContext, "正在加载中...");
                    return;
                }
                isRunning = true;
                mAdapter.clear();
                mPresenter.get(Url.NEW_FANS);
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new NewFansAdapter();
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.addItemDecoration(new SGSpacingItemDecoration(1, mContext.getResources().getDimensionPixelOffset(R.dimen.v4dp)));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_with_refresh_layout;
    }

    @Override
    public void success(final List<NewFansBean> result) {
        mRefreshLayout.finishRefresh(true);
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.add(result);
                isRunning = false;
            }
        }, 360);
    }

    @Override
    public void error(String error) {
        SnackbarUtils.create(mContext, error);
        isRunning = false;
        mRefreshLayout.finishRefresh(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.unsubscribe();
        }
    }
}
