package star.iota.sakura.ui.fans.oldfans;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import star.iota.sakura.utils.MessageBar;


public class FansFragment extends BaseFragment implements PVContract.View<List<FansBean>> {


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private FansAdapter mAdapter;

    private FansPresenter mPresenter;
    private boolean isRunning;
    private String index;

    public static FansFragment newInstance(String index) {
        FansFragment fragment = new FansFragment();
        Bundle bundle = new Bundle();
        bundle.putString("index", index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void init() {
        index = getArguments().getString("index");
        if (index == null) {
            MessageBar.create(mContext, "获取数据错误，请返回重试");
            return;
        }
        setTitle(index + "番组");
        initPresenter();
        initRecyclerView();
        initRefreshLayout();
    }

    private void initPresenter() {
        isRunning = false;
        mPresenter = new FansPresenter(this);
    }

    private void initRefreshLayout() {
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
                mPresenter.get(Url.FANS + index + ".json");
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new FansAdapter();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_with_refresh_layout;
    }

    @Override
    public void success(final List<FansBean> result) {
        mRefreshLayout.finishRefresh(true);
        mAdapter.add(result);
        isRunning = false;
    }

    @Override
    public void error(String error) {
        MessageBar.create(mContext, "可能發生錯誤：" + error);
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
