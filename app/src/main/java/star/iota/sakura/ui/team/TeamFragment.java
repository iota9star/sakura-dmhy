package star.iota.sakura.ui.team;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import star.iota.sakura.R;
import star.iota.sakura.Url;
import star.iota.sakura.base.BaseAdapter;
import star.iota.sakura.base.BaseFragment;
import star.iota.sakura.base.PVContract;
import star.iota.sakura.utils.MessageBar;


public abstract class TeamFragment extends BaseFragment implements PVContract.View<List<TeamBean>> {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private BaseAdapter mAdapter;

    private TeamPresenter mPresenter;
    private boolean isRunning;

    @Override
    protected void init() {
        initPresenter();
        initRecyclerView();
        initRefreshLayout();
    }

    private void initPresenter() {
        isRunning = false;
        mPresenter = new TeamPresenter(this);
    }

    private void initRefreshLayout() {
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            if (isRunning) {
                MessageBar.create(mContext, "正在加载中...");
                return;
            }
            isRunning = true;
            mAdapter.clear();
            mPresenter.get(Url.TEAM);
        });
    }

    private void initRecyclerView() {
        mAdapter = getAdapter();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }

    protected abstract BaseAdapter getAdapter();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_with_refresh_layout;
    }


    @Override
    public void success(final List<TeamBean> result) {
        mRefreshLayout.finishRefresh(true);
        mAdapter.add(result);
        isRunning = false;
    }

    @Override
    public void error(String error) {
        MessageBar.create(mContext, error);
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

    @Override
    public void isCache() {
        MessageBar.create(getActivity(),"请注意，以下内容来自缓存");
    }
}
