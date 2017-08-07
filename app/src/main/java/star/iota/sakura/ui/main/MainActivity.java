package star.iota.sakura.ui.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import moe.feng.alipay.zerosdk.AlipayZeroSdk;
import star.iota.sakura.Menus;
import star.iota.sakura.R;
import star.iota.sakura.Url;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.base.BaseFragment;
import star.iota.sakura.glide.GlideApp;
import star.iota.sakura.ui.about.AboutFragment;
import star.iota.sakura.ui.fans.newfans.NewFansFragment;
import star.iota.sakura.ui.index.IndexFragment;
import star.iota.sakura.ui.local.fan.LocalFanFragment;
import star.iota.sakura.ui.local.fans.LocalSubsFragment;
import star.iota.sakura.ui.post.PostFragment;
import star.iota.sakura.utils.ConfigUtils;
import star.iota.sakura.utils.SnackbarUtils;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private Drawer mDrawer;
    private int mCurrentFragmentId;

    @Override
    protected void init() {
        setSupportActionBar(mToolbar);
        initDrawer();
        initDrawerEvent();
        isShowDonationDialog();
    }

    private void isShowDonationDialog() {
        if ((ConfigUtils.getOpenCount(mContext) % 10 == 0 || ConfigUtils.getOpenCount(mContext) == 3)
                && ConfigUtils.isShowDonation(mContext)) {
            mToolbar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    donation();
                }
            }, 3600);
        }
    }

    private void donation() {
        View view = getLayoutInflater().inflate(R.layout.dialog_donation, null);
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setView(view)
                .setNegativeButton("下次吧", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("不再提醒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ConfigUtils.saveDonationStatus(mContext, false);
                        dialogInterface.dismiss();
                        SnackbarUtils.create(mContext, "如果想要支持我的话，可以在“关于”里面查看");
                    }
                })
                .create();
        view.findViewById(R.id.linear_layout_donation_alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AlipayZeroSdk.hasInstalledAlipayClient(mContext)) {
                    AlipayZeroSdk.startAlipayClient(MainActivity.this, getResources().getString(R.string.alipay_code));
                } else {
                    SnackbarUtils.create(mContext, "你可能没有安装支付宝");
                }
            }
        });
        view.findViewById(R.id.linear_layout_donation_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mContext.getString(R.string.qq_pay_code)));
                startActivity(intent);
            }
        });
        view.findViewById(R.id.linear_layout_donation_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mContext.getString(R.string.wechat_pay_code)));
                startActivity(intent);
            }
        });
        view.findViewById(R.id.linear_layout_grade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + mContext.getPackageName()));
                startActivity(intent);
            }
        });
        dialog.show();
    }

    @Override
    protected void setFirstFragment() {
        String keywords = getIntent().getStringExtra(SearchManager.QUERY);
        if (keywords != null) {
            mCurrentFragmentId = -1;
            showFragment(PostFragment.newInstance(Url.SEARCH, "?keyword=" + keywords, "搜索：" + keywords));
        } else {
            mCurrentFragmentId = Menus.NEWS_ID;
            showFragment(PostFragment.newInstance(Url.NEWS, "", Menus.NEWS));
        }
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.frame_layout_fragment_container;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    private void initDrawer() {
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withItemAnimator(new LandingAnimator())
                .withHeader(R.layout.drawer_header_view)
                .withHeaderDivider(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(Menus.NEWS).withIdentifier(Menus.NEWS_ID).withIcon(Menus.NEWS_ICON).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withName(Menus.NEW_FANS).withIdentifier(Menus.NEW_FANS_ID).withIcon(Menus.NEW_FANS_ICON).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withName(Menus.INDEX).withIdentifier(Menus.INDEX_ID).withIcon(Menus.INDEX_ICON).withIconTintingEnabled(true),
                        new ExpandableDrawerItem().withIcon(Menus.CATEGORY_ICON).withName(Menus.CATEGORY).withIdentifier(Menus.CATEGORY_ID).withSubItems(getCategory()).withSelectable(false).withIconTintingEnabled(true),
                        new ExpandableDrawerItem().withIcon(Menus.COLLECTION_ICON).withName(Menus.COLLECTION).withIdentifier(Menus.COLLECTION_ID).withSubItems(getCollection()).withSelectable(false).withIconTintingEnabled(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(Menus.ABOUT).withIdentifier(Menus.ABOUT_ID).withIcon(Menus.ABOUT_ICON).withIconTintingEnabled(true)
                )
                .withSelectedItem(-1)
                .build();
        View header = mDrawer.getHeader();
        ImageView banner = ButterKnife.findById(header, R.id.image_view_banner);
        GlideApp.with(mContext)
                .load(getResources().getString(R.string.banner))
                .into(banner);
    }

    private ArrayList<IDrawerItem> getCategory() {
        ArrayList<IDrawerItem> menu = new ArrayList<>();
        menu.add(new SecondaryDrawerItem().withIcon(Menus.ANIME_ICON).withIconTintingEnabled(true).withIdentifier(Menus.ANIME_ID).withName(Menus.ANIME));
        menu.add(new SecondaryDrawerItem().withIcon(Menus.MANGA_ICON).withIconTintingEnabled(true).withIdentifier(Menus.MANGA_ID).withName(Menus.MANGA));
        menu.add(new SecondaryDrawerItem().withIcon(Menus.MUSIC_ICON).withIconTintingEnabled(true).withIdentifier(Menus.MUSIC_ID).withName(Menus.MUSIC));
        menu.add(new SecondaryDrawerItem().withIcon(Menus.JP_TV_ICON).withIconTintingEnabled(true).withIdentifier(Menus.JP_TV_ID).withName(Menus.JP_TV));
        menu.add(new SecondaryDrawerItem().withIcon(Menus.RAW_ICON).withIconTintingEnabled(true).withIdentifier(Menus.RAW_ID).withName(Menus.RAW));
        menu.add(new SecondaryDrawerItem().withIcon(Menus.GAME_ICON).withIconTintingEnabled(true).withIdentifier(Menus.GAME_ID).withName(Menus.GAME));
        menu.add(new SecondaryDrawerItem().withIcon(Menus.TOKUSATSU_ICON).withIconTintingEnabled(true).withIdentifier(Menus.TOKUSATSU_ID).withName(Menus.TOKUSATSU));
        menu.add(new SecondaryDrawerItem().withIcon(Menus.OTHER_ICON).withIconTintingEnabled(true).withIdentifier(Menus.OTHER_ID).withName(Menus.OTHER));
        return menu;
    }

    private ArrayList<IDrawerItem> getCollection() {
        ArrayList<IDrawerItem> menu = new ArrayList<>();
        menu.add(new SecondaryDrawerItem().withIcon(Menus.COLLECTION_FAN_ICON).withIconTintingEnabled(true).withIdentifier(Menus.COLLECTION_FAN_ID).withName(Menus.COLLECTION_FAN));
        menu.add(new SecondaryDrawerItem().withIcon(Menus.COLLECTION_SUBS_ICON).withIconTintingEnabled(true).withIdentifier(Menus.COLLECTION_SUBS_ID).withName(Menus.COLLECTION_SUBS));
        return menu;
    }

    private void initDrawerEvent() {
        mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem dItem) {
                BaseFragment currentFragment = null;
                int identifier = (int) dItem.getIdentifier();
                if (identifier == mCurrentFragmentId || identifier == 999) return false;
                mCurrentFragmentId = identifier;
                switch (identifier) {
                    case Menus.NEW_FANS_ID:
                        currentFragment = new NewFansFragment();
                        break;
                    case Menus.INDEX_ID:
                        currentFragment = new IndexFragment();
                        break;
                    case Menus.NEWS_ID:
                        currentFragment = PostFragment.newInstance(Url.NEWS, "", Menus.NEWS);
                        break;
                    case Menus.ANIME_ID:
                        currentFragment = PostFragment.newInstance(Url.ANIME, "", Menus.ANIME);
                        break;
                    case Menus.MANGA_ID:
                        currentFragment = PostFragment.newInstance(Url.MANGA, "", Menus.MANGA);
                        break;
                    case Menus.MUSIC_ID:
                        currentFragment = PostFragment.newInstance(Url.MUSIC, "", Menus.MUSIC);
                        break;
                    case Menus.JP_TV_ID:
                        currentFragment = PostFragment.newInstance(Url.JP_TV, "", Menus.JP_TV);
                        break;
                    case Menus.RAW_ID:
                        currentFragment = PostFragment.newInstance(Url.RAW, "", Menus.RAW);
                        break;
                    case Menus.GAME_ID:
                        currentFragment = PostFragment.newInstance(Url.GAME, "", Menus.GAME);
                        break;
                    case Menus.TOKUSATSU_ID:
                        currentFragment = PostFragment.newInstance(Url.TOKUSATSU, "", Menus.TOKUSATSU);
                        break;
                    case Menus.OTHER_ID:
                        currentFragment = PostFragment.newInstance(Url.OTHER, "", Menus.OTHER);
                        break;
                    case Menus.COLLECTION_FAN_ID:
                        currentFragment = LocalFanFragment.newInstance();
                        break;
                    case Menus.COLLECTION_SUBS_ID:
                        currentFragment = LocalSubsFragment.newInstance();
                        break;
                    case Menus.ABOUT_ID:
                        currentFragment = new AboutFragment();
                        break;
                }
                removeFragmentContainerChildrenViews();
                showFragment(currentFragment);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}
