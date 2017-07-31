package star.iota.sakura.ui.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import star.iota.sakura.Menus;
import star.iota.sakura.R;
import star.iota.sakura.Url;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.base.BaseFragment;
import star.iota.sakura.broadcast.NetStatusBroadcastReceiver;
import star.iota.sakura.glide.GlideApp;
import star.iota.sakura.ui.about.AboutFragment;
import star.iota.sakura.ui.fans.newfans.NewFansFragment;
import star.iota.sakura.ui.index.IndexFragment;
import star.iota.sakura.ui.post.PostFragment;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private Drawer drawer;
    private int currentFragmentId;
    private NetStatusBroadcastReceiver mNetStatusBroadcastReceiver;

    private void initNetBroadcastReceiver() {
        mNetStatusBroadcastReceiver = new NetStatusBroadcastReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetStatusBroadcastReceiver, mFilter);
    }

    @Override
    protected void init() {
        setSupportActionBar(mToolbar);
        initDrawer();
        initDrawerEvent();
        initNetBroadcastReceiver();
    }

    @Override
    protected void setFirstFragment() {
        String keywords = getIntent().getStringExtra(SearchManager.QUERY);
        if (keywords != null) {
            currentFragmentId = -1;
            showFragment(PostFragment.newInstance(Url.SEARCH, "?keyword=" + keywords, "搜索：" + keywords));
        } else {
            currentFragmentId = Menus.NEWS_ID;
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
        drawer = new DrawerBuilder()
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
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(Menus.ABOUT).withIdentifier(Menus.ABOUT_ID).withIcon(Menus.ABOUT_ICON).withSelectable(false).withIconTintingEnabled(true)
                )
                .withSelectedItem(-1)
                .build();
        View header = drawer.getHeader();
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

    private void initDrawerEvent() {
        drawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem dItem) {
                BaseFragment currentFragment = null;
                int identifier = (int) dItem.getIdentifier();
                if (identifier == currentFragmentId || identifier == 999) return false;
                currentFragmentId = identifier;
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
    protected void onDestroy() {
        super.onDestroy();
        if (mNetStatusBroadcastReceiver != null) {
            unregisterReceiver(mNetStatusBroadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
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
