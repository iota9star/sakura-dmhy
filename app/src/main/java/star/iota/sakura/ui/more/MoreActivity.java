package star.iota.sakura.ui.more;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.blurry.Blurry;
import star.iota.sakura.R;
import star.iota.sakura.Url;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.base.PagerAdapter;
import star.iota.sakura.database.FanDAO;
import star.iota.sakura.database.FanDAOImpl;
import star.iota.sakura.glide.GlideApp;
import star.iota.sakura.ui.fans.FanBean;
import star.iota.sakura.ui.fans.SubBean;
import star.iota.sakura.ui.post.PostFragment;
import star.iota.sakura.utils.MessageBar;

public class MoreActivity extends BaseActivity {

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.image_view_cover)
    ImageView mImageViewCover;
    @BindView(R.id.image_view_banner)
    ImageView mImageViewBanner;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.text_view_name)
    TextView mTextViewName;
    @BindView(R.id.text_view_official)
    TextView mTextViewOfficial;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Override
    protected void init() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final FanBean bean = getIntent().getParcelableExtra("bean");
        if (bean == null) {
            MessageBar.create(mContext, "未獲取到數據，3秒後返回前一界面");
            mViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3000);
            return;
        }
        initHeader(bean);
        initViewPager(bean);
        initEvent(bean);
    }

    private void initEvent(final FanBean bean) {
        final FanDAOImpl fanDAO = new FanDAOImpl(mContext);
        Observable.just(fanDAO)
                .map(new Function<FanDAO, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull FanDAO fanDAO) throws Exception {
                        return fanDAO.exist(bean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            mFab.setImageResource(R.drawable.ic_favorite_white_24dp);
                        } else {
                            mFab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        mFab.setOnClickListener(new View.OnClickListener() {

            private boolean success = false;

            @Override
            public void onClick(View view) {
                Observable.just(bean)
                        .map(new Function<FanBean, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull FanBean bean) throws Exception {
                                return fanDAO.exist(bean);
                            }
                        })
                        .map(new Function<Boolean, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    success = fanDAO.delete(bean);
                                } else {
                                    success = fanDAO.save(bean);
                                }
                                return aBoolean;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (!success) {
                                    MessageBar.create(mContext, "操作失敗，請重試");
                                    return;
                                }
                                if (!aBoolean) {
                                    MessageBar.create(mContext, "收藏成功：" + bean.getName());
                                    mFab.setImageResource(R.drawable.ic_favorite_white_24dp);
                                } else {
                                    MessageBar.create(mContext, "取消收藏成功：" + bean.getName());
                                    mFab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                MessageBar.create(mContext, "操作失敗，請重試：" + throwable.getMessage());
                            }
                        });
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private boolean isHide = true;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offset = appBarLayout.getTotalScrollRange() + verticalOffset;
                if (offset < getResources().getDimensionPixelOffset(R.dimen.v8dp)) {
                    mTabLayout.setPadding(0, 0, 0, 0);
                } else {
                    mTabLayout.setPadding(getResources().getDimensionPixelOffset(R.dimen.v96dp), 0, 0, 0);
                }
                if (offset <= getResources().getDimensionPixelOffset(R.dimen.v56dp)) {
                    if (isHide) {
                        mCollapsingToolbarLayout.setTitle(bean.getName());
                        isHide = false;
                    }
                } else {
                    if (!isHide) {
                        mCollapsingToolbarLayout.setTitle("");
                        isHide = true;
                    }
                }
            }
        });
    }

    private void initHeader(FanBean bean) {
        GlideApp.with(mContext)
                .asBitmap()
                .load(bean.getCover().replace("http://", "https://"))
                .error(R.drawable.bg_sakura)
                .fallback(R.drawable.bg_sakura)
                .placeholder(R.drawable.bg_sakura)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                        Blurry.with(mContext).from(bitmap).into(mImageViewBanner);
                        return false;
                    }
                })
                .into(mImageViewCover);
        mTextViewName.setText(bean.getName());
        mTextViewOfficial.setText(String.format("官網：%s", bean.getOfficial()));
    }

    private void initViewPager(FanBean bean) {
        List<String> titles = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();
        titles.add("全部");
        fragments.add(PostFragment.newInstance(Url.SEARCH, "?keyword=" + bean.getKeyword(), ""));
        List<SubBean> subs = bean.getSubs();
        if (subs != null && subs.size() > 0) {
            for (SubBean sub : subs) {
                titles.add(sub.getName());
                PostFragment fragment;
                String url = sub.getUrl();
                if (url.contains("http")) {
                    String after = url.substring(url.indexOf("/topics/list") + 12, url.length());
                    fragment = PostFragment.newInstance(Url.SEARCH, after, "");
                } else {
                    fragment = PostFragment.newInstance(Url.SEARCH, "?keyword=" + bean.getKeyword() + "&team_id=" + url, "");
                }
                fragments.add(fragment);
            }
        }
        mViewPager.setOffscreenPageLimit(fragments.size());
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), fragments, titles));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_more;
    }
}
