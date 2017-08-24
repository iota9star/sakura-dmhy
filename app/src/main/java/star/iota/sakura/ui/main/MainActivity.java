package star.iota.sakura.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liuguangqiang.cookie.OnActionClickListener;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import moe.feng.alipay.zerosdk.AlipayZeroSdk;
import star.iota.sakura.Menus;
import star.iota.sakura.R;
import star.iota.sakura.Url;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.base.BaseFragment;
import star.iota.sakura.database.FanDAOImpl;
import star.iota.sakura.database.SubsDAOImpl;
import star.iota.sakura.glide.GlideApp;
import star.iota.sakura.ui.about.AboutFragment;
import star.iota.sakura.ui.fans.FanBean;
import star.iota.sakura.ui.fans.newfans.NewFansFragment;
import star.iota.sakura.ui.index.IndexFragment;
import star.iota.sakura.ui.local.fan.LocalFanFragment;
import star.iota.sakura.ui.local.subs.LocalSubsFragment;
import star.iota.sakura.ui.post.PostBean;
import star.iota.sakura.ui.post.PostFragment;
import star.iota.sakura.ui.rss.RSSPostFragment;
import star.iota.sakura.ui.team.def.TeamDefaultFragment;
import star.iota.sakura.ui.team.rss.TeamRSSFragment;
import star.iota.sakura.utils.ConfigUtils;
import star.iota.sakura.utils.FileUtils;
import star.iota.sakura.utils.MessageBar;

public class MainActivity extends BaseActivity {

    public static final int FAN_IMPORT_CODE = 1;
    public static final int SUBS_IMPORT_CODE = 2;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private int sort_id = 0;
    private Drawer mDrawer;
    private int mCurrentFragmentId;
    private SearchView mSearchView;

    @Override
    protected void init() {
        setSupportActionBar(mToolbar);
        initDrawer();
        initDrawerEvent();
        isShowDonationDialog();
        checkPermission();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String filePath = FileUtils.getUriRawPath(mContext, uri);
            if (filePath == null) {
                MessageBar.create(mContext, "獲取備份文件路徑出現未知錯誤");
                return;
            }
            switch (requestCode) {
                case FAN_IMPORT_CODE:
                    importFan(filePath);
                    break;
                case SUBS_IMPORT_CODE:
                    importSubs(filePath);
                    break;
            }
        } else {
            MessageBar.create(mContext, "獲取備份文件錯誤，錯誤碼：" + resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importSubs(String filePath) {
        Observable.just(filePath)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        FileInputStream fis = new FileInputStream(new File(s));
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        InputStreamReader isr = new InputStreamReader(bis);
                        BufferedReader br = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String temp;
                        while ((temp = br.readLine()) != null) {
                            sb.append(temp);
                        }
                        br.close();
                        isr.close();
                        bis.close();
                        fis.close();
                        return sb.toString();
                    }
                })
                .map(new Function<String, List<PostBean>>() {
                    @Override
                    public List<PostBean> apply(@NonNull String s) throws Exception {
                        return new Gson().fromJson(s, new TypeToken<List<PostBean>>() {
                        }.getType());
                    }
                })
                .map(new Function<List<PostBean>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull List<PostBean> postBeen) throws Exception {
                        return new SubsDAOImpl(mContext).save(postBeen);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            MessageBar.create(mContext, "導入成功，請刷新");
                        } else {
                            MessageBar.create(mContext, "由於未知原因導入失敗");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        MessageBar.create(mContext, "導入失敗，請檢查導入的文件是否正確：" + throwable.getMessage());
                    }
                });
    }

    private void importFan(String filePath) {
        Observable.just(filePath)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        FileInputStream fis = new FileInputStream(new File(s));
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        InputStreamReader isr = new InputStreamReader(bis);
                        BufferedReader br = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String temp;
                        while ((temp = br.readLine()) != null) {
                            sb.append(temp);
                        }
                        br.close();
                        isr.close();
                        bis.close();
                        fis.close();
                        return sb.toString();
                    }
                })
                .map(new Function<String, List<FanBean>>() {
                    @Override
                    public List<FanBean> apply(@NonNull String s) throws Exception {
                        return new Gson().fromJson(s, new TypeToken<List<FanBean>>() {
                        }.getType());
                    }
                })
                .map(new Function<List<FanBean>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull List<FanBean> fanBeen) throws Exception {
                        return new FanDAOImpl(mContext).save(fanBeen);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            MessageBar.create(mContext, "導入成功，請刷新");
                        } else {
                            MessageBar.create(mContext, "由於未知原因導入失敗");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        MessageBar.create(mContext, "導入失敗，請檢查導入的文件是否正確：" + throwable.getMessage());
                    }
                });
    }

    private void checkPermission() {
        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean granted) throws Exception {
                        if (!granted) {
                            MessageBar.create(mContext, "您拒絕了文件寫入權限，備份可能會出現錯誤，是否前往開啓", "好的", new OnActionClickListener() {
                                @Override
                                public void onClick() {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }

    private void isShowDonationDialog() {
        long openCount = ConfigUtils.getOpenCount(mContext);
        if ((openCount % 16 == 0 || openCount == 5)
                && ConfigUtils.isShowDonation(mContext)) {
            mToolbar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    donation();
                }
            }, 3600);
        } else if (openCount % 100 == 0) {
            MessageBar.create(mContext, "這是您打開的第 " + openCount + " 次，將冒昧的顯示捐贈頁面");
            mToolbar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    donation();
                }
            }, 3600);
        }
    }

    private void donation() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_donation, null);
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
                        MessageBar.create(mContext, "如果想要支持我的話，可以在“關於本軟”中查看");
                    }
                })
                .create();
        ((TextView) view.findViewById(R.id.text_view_please)).setMovementMethod(LinkMovementMethod.getInstance());
        view.findViewById(R.id.linear_layout_donation_alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AlipayZeroSdk.hasInstalledAlipayClient(mContext)) {
                    AlipayZeroSdk.startAlipayClient(MainActivity.this, getResources().getString(R.string.alipay_code));
                } else {
                    MessageBar.create(mContext, "您可能沒有安裝支付寶");
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
        mCurrentFragmentId = Menus.NEWS_ID;
        showFragment(PostFragment.newInstance(Url.NEWS, "", Menus.NEWS));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!intent.getAction().equals(Intent.ACTION_SEARCH)) {
            return;
        }
        String keywords = intent.getStringExtra(SearchManager.QUERY);
        mCurrentFragmentId = -1;
        showFragment(PostFragment.newInstance(Url.SEARCH, "?keyword=" + keywords + "&sort_id=" + sort_id, "搜索：" + keywords));
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
                        new PrimaryDrawerItem().withName(Menus.TEAM_DEFAULT).withIdentifier(Menus.TEAM_DEFAULT_ID).withIcon(Menus.TEAM_DEFAULT_ICON).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withName(Menus.NEWS).withIdentifier(Menus.NEWS_ID).withIcon(Menus.NEWS_ICON).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withName(Menus.NEW_FANS).withIdentifier(Menus.NEW_FANS_ID).withIcon(Menus.NEW_FANS_ICON).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withName(Menus.INDEX).withIdentifier(Menus.INDEX_ID).withIcon(Menus.INDEX_ICON).withIconTintingEnabled(true),
                        new ExpandableDrawerItem().withIcon(Menus.RSS_ICON).withName(Menus.RSS).withIdentifier(Menus.RSS_ID).withSubItems(getRSS()).withSelectable(false).withIconTintingEnabled(true),
                        new ExpandableDrawerItem().withIcon(Menus.CATEGORY_ICON).withName(Menus.CATEGORY).withIdentifier(Menus.CATEGORY_ID).withSubItems(getCategory()).withSelectable(false).withIconTintingEnabled(true),
                        new ExpandableDrawerItem().withIcon(Menus.COLLECTION_ICON).withName(Menus.COLLECTION).withIdentifier(Menus.COLLECTION_ID).withSubItems(getCollection()).withSelectable(false).withIconTintingEnabled(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(Menus.ABOUT).withIdentifier(Menus.ABOUT_ID).withIcon(Menus.ABOUT_ICON).withIconTintingEnabled(true)
                )
                .withSelectedItem(Menus.NEWS_ID)
                .build();
        View header = mDrawer.getHeader();
        ImageView banner = header.findViewById(R.id.image_view_banner);
        GlideApp.with(mContext)
                .load(getResources().getString(R.string.banner))
                .into(banner);
    }

    private ArrayList<IDrawerItem> getCategory() {
        ArrayList<IDrawerItem> menu = new ArrayList<>();
        menu.add(new SecondaryDrawerItem().withIcon(Menus.ANIME_ICON).withIconTintingEnabled(true).withIdentifier(Menus.ANIME_ID).withName(Menus.ANIME));
        menu.add(new SecondaryDrawerItem().withIcon(Menus.SEASON_ICON).withIconTintingEnabled(true).withIdentifier(Menus.SEASON_ID).withName(Menus.SEASON));
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

    private ArrayList<IDrawerItem> getRSS() {
        ArrayList<IDrawerItem> menu = new ArrayList<>();
        menu.add(new SecondaryDrawerItem().withIcon(Menus.RSS_NEWS_ICON).withIconTintingEnabled(true).withIdentifier(Menus.RSS_NEWS_ID).withName(Menus.RSS_NEWS));
        menu.add(new SecondaryDrawerItem().withIcon(Menus.RSS_SUBS_ICON).withIconTintingEnabled(true).withIdentifier(Menus.RSS_SUBS_ID).withName(Menus.RSS_SUBS));
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
                    case Menus.TEAM_DEFAULT_ID:
                        currentFragment = new TeamDefaultFragment();
                        break;
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
                    case Menus.SEASON_ID:
                        currentFragment = PostFragment.newInstance(Url.SEASON, "", Menus.SEASON);
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
                    case Menus.RSS_NEWS_ID:
                        currentFragment = RSSPostFragment.newInstance(Url.RSS_NEWS, "/rss.xml", Menus.RSS + " * " + Menus.RSS_NEWS);
                        break;
                    case Menus.RSS_SUBS_ID:
                        currentFragment = new TeamRSSFragment();
                        break;
                }
                removeFragmentContainerChildrenViews();
                showFragment(currentFragment);
                return false;
            }
        });
    }

    private void showPopUpWindow() {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);
        listPopupWindow.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1,
                new String[]{
                        "所有",
                        Menus.ANIME,
                        Menus.SEASON,
                        Menus.MANGA,
                        Menus.MUSIC,
                        Menus.JP_TV,
                        Menus.RAW,
                        Menus.GAME,
                        Menus.TOKUSATSU,
                        Menus.OTHER
                }));
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                String category = "";
                switch (pos) {
                    case 0:
                        sort_id = 0;
                        category = "所有";
                        break;
                    case 1:
                        sort_id = 2;
                        category = Menus.ANIME;
                        break;
                    case 2:
                        sort_id = 31;
                        category = Menus.SEASON;
                        break;
                    case 3:
                        sort_id = 3;
                        category = Menus.MANGA;
                        break;
                    case 4:
                        sort_id = 4;
                        category = Menus.MUSIC;
                        break;
                    case 5:
                        sort_id = 6;
                        category = Menus.JP_TV;
                        break;
                    case 6:
                        sort_id = 7;
                        category = Menus.RAW;
                        break;
                    case 7:
                        sort_id = 9;
                        category = Menus.GAME;
                        break;
                    case 8:
                        sort_id = 12;
                        category = Menus.TOKUSATSU;
                        break;
                    case 9:
                        sort_id = 1;
                        category = Menus.OTHER;
                        break;
                }
                mSearchView.setQueryHint("在" + category + "分類中搜索...");
                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.setWidth((int) mContext.getResources().getDimension(R.dimen.v68dp));
        listPopupWindow.setAnchorView(findViewById(R.id.action_category));
        listPopupWindow.setHorizontalOffset((int) mContext.getResources().getDimension(R.dimen.v6dp));
        listPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_popup_window_category));
        listPopupWindow.setModal(true);
        listPopupWindow.show();
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
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setQueryHint("在所有分類中搜索...");
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_category:
                showPopUpWindow();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}
