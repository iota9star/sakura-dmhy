package star.iota.sakura.ui.rss;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.OnRefreshLoadmoreListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseFragment;
import star.iota.sakura.base.PVContract;
import star.iota.sakura.utils.MessageBar;


public class RSSPostFragment extends BaseFragment implements PVContract.View<List<RSSPostBean>> {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private RSSPostAdapter mAdapter;
    private RSSPostPresenter mPresenter;
    private boolean isRunning;
    private boolean isLoadMore;
    private int mPage;
    private String mUrl;
    private String mParameter;
    private String mTitle;

    public static RSSPostFragment newInstance(String url, String parameter, String title) {
        RSSPostFragment fragment = new RSSPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("parameter", parameter);
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString("url");
        mParameter = getArguments().getString("parameter");
        mTitle = getArguments().getString("title");
    }

    @Override
    protected void init() {
        setToolbarTitle(mTitle);
        initPresenter();
        initRecyclerView();
        initRefreshLayout();
    }

    private void initPresenter() {
        isRunning = false;
        isLoadMore = false;
        mPage = 1;
        mPresenter = new RSSPostPresenter(this);
    }

    private void initRefreshLayout() {
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (isRunning) {
                    MessageBar.create(mContext, "正在加载中...");
                    return;
                }
                isRunning = true;
                isLoadMore = true;
                String url = mUrl + mPage + mParameter;
                if (url.contains("+team_id%3A")) {
                    url = url.replace("+team_id%3A", "&team_id=");
                }
                mPresenter.get(url);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (isRunning) {
                    MessageBar.create(mContext, "正在加载中...");
                    return;
                }
                mPage = 1;
                isRunning = true;
                isLoadMore = false;
                mAdapter.clear();
                mPresenter.get(mUrl + mPage + mParameter);
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new RSSPostAdapter();
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_with_refresh_layout;
    }

    @Override
    public void success(final List<RSSPostBean> result) {
        if (isLoadMore) {
            mRefreshLayout.finishLoadmore(true);
        } else {
            mPage = 1;
            mRefreshLayout.finishRefresh(true);
        }
        mAdapter.add(result);
        isRunning = false;
        if (result.size() < 1) {
            MessageBar.create(mContext, "未檢索到更多數據...");
        } else {
            mPage++;
        }
    }

    @Override
    public void error(String error) {
        MessageBar.create(mContext, error);
        isRunning = false;
        if (isLoadMore) {
            mRefreshLayout.finishLoadmore(false);
        } else {
            mRefreshLayout.finishRefresh(false);
        }
    }

    @Override
    public void isCache() {
        MessageBar.create(getActivity(), "请注意，以下内容来自缓存");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.unsubscribe();
        }
    }
}
