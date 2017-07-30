package star.iota.sakura.ui.index;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import star.iota.sakura.R;
import star.iota.sakura.Url;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.base.BaseFragment;
import star.iota.sakura.base.PVContract;
import star.iota.sakura.utils.SnackbarUtils;


public class IndexFragment extends BaseFragment implements PVContract.View<IndexBean> {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private IndexAdapter mAdapter;

    private IndexPresenter mPresenter;
    private boolean isRunning;

    @Override
    protected void init() {
        setTitle("完結番組");
        initPresenter();
        initRecyclerView();
        initRefreshLayout();
    }

    private void initPresenter() {
        isRunning = false;
        mPresenter = new IndexPresenter(this);
    }

    private void initRefreshLayout() {
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (isRunning) {
                    SnackbarUtils.create(mContext, "还在加载中...");
                    return;
                }
                isRunning = true;
                mAdapter.clear();
                mPresenter.get(Url.INDEX);
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new IndexAdapter((BaseActivity) getActivity());
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_with_refresh_layout;
    }

    @Override
    public void success(final IndexBean result) {
        mRefreshLayout.finishRefresh(true);
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.add(result.getYears());
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
