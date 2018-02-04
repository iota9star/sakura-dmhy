package star.iota.sakura.ui.post;

import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx2.adapter.ObservableResponse;
import com.zzhoujay.richtext.RichText;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.base.BaseViewHolder;
import star.iota.sakura.database.SubsDAOImpl;
import star.iota.sakura.ui.more.MoreActivity;
import star.iota.sakura.utils.MessageBar;
import star.iota.sakura.utils.SimpleUtils;


class PostViewHolder extends BaseViewHolder<PostBean> {
    private final CompositeDisposable mCompositeDisposable;
    @BindView(R.id.text_view_title)
    TextView mTextViewTitle;
    @BindView(R.id.text_view_category)
    TextView mTextViewCategory;
    @BindView(R.id.text_view_date)
    TextView mTextViewDate;
    @BindView(R.id.text_view_size)
    TextView mTextViewSize;
    @BindView(R.id.button_link)
    Button mButtonLink;
    @BindView(R.id.button_magnet)
    Button mButtonMagnet;
    @BindView(R.id.button_sub)
    Button mButtonSub;
    @BindView(R.id.button_collection)
    Button mButtonCollection;

    PostViewHolder(View itemView) {
        super(itemView);
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void bindView(final PostBean bean) {
        mTextViewCategory.setText(bean.getCategory());
        mTextViewTitle.setText(("/" + bean.getTitle().replaceAll("]\\s*\\[|\\[|]|】\\s*【|】|【", "/") + "/").replaceAll("(/\\s*/+)+", "/"));
        mTextViewTitle.setOnClickListener(view -> SimpleUtils.openUrl(mContext, bean.getUrl()));
        mTextViewDate.setText(bean.getDate());
        mTextViewSize.setText(bean.getSize());
        if (mContext instanceof MoreActivity) {
            mButtonSub.setVisibility(View.GONE);
            mButtonSub.setOnClickListener(null);
        } else {
            mButtonSub.setVisibility(View.VISIBLE);
            if (bean.getSub() != null) {
                mButtonSub.setOnClickListener(view -> ((BaseActivity) mContext).addFragment(
                        PostFragment.newInstance(bean.getSub().getUrl() + "/page/",
                                "",
                                bean.getSub().getName())));
            } else {
                mButtonSub.setOnClickListener(view -> MessageBar.create(mContext, "該發佈者無鏈接"));
            }
        }
        mButtonMagnet.setOnClickListener(view -> SimpleUtils.copy(mContext, bean.getMagnet()));
        mButtonLink.setOnClickListener(view -> showInfo(bean.getUrl()));
        mButtonCollection.setOnClickListener(view -> Observable.just(new SubsDAOImpl(mContext))
                .filter(subsDAO -> {
                    boolean exist = subsDAO.exist(bean.getUrl());
                    if (exist) {
                        MessageBar.create(mContext, "已存在：" +("/" + bean.getTitle().replaceAll("]\\s*\\[|\\[|]|】\\s*【|】|【", "/") + "/").replaceAll("(/\\s*/+)+", "/"));
                    }
                    return !exist;
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(subsDAO -> subsDAO.save(bean))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        MessageBar.create(mContext, "收藏成功：" +("/" + bean.getTitle().replaceAll("]\\s*\\[|\\[|]|】\\s*【|】|【", "/") + "/").replaceAll("(/\\s*/+)+", "/"));
                    } else {
                        MessageBar.create(mContext, "收藏過程中可能出現錯誤");
                    }
                }, throwable -> MessageBar.create(mContext, "收藏過程中可能出現錯誤：" + throwable.getMessage())));
    }

    private void showInfo(final String url) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_info_with_progressbar, null);
        final TextView info = view.findViewById(R.id.text_view_info);
        final ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setView(view)
                .setTitle(mContext.getString(R.string.app_name))
                .setIcon(R.mipmap.app_icon)
                .setNegativeButton("瀏覽器打開", (dialogInterface, i) -> SimpleUtils.openUrl(mContext, url))
                .create();
        dialog.setOnDismissListener(dialogInterface -> mCompositeDisposable.clear());
        dialog.show();
        mCompositeDisposable.add(
                OkGo.<String>get(url)
                        .converter(new StringConvert())
                        .adapt(new ObservableResponse<>())
                        .subscribeOn(Schedulers.io())
                        .map(s -> {
                            Elements select = Jsoup.parse(s.body()).select("div.main div.topic-main");
                            select.select("#before-comment").remove();
                            select.select("#before-comment").remove();
                            select.select("#recent-commnet").remove();
                            select.select("div.topic-title div.info.resource-info.right > ul > li.share")
                                    .remove();
                            select.select("div.file_list > ul > li > img")
                                    .remove();
                            select.select("div.file_list")
                                    .before("<strong>文件列表:</strong><br/><br/>");
                            select.select("#tabs-1 > div > ul > li > span.bt_file_size")
                                    .before("&nbsp;-&nbsp;");
                            select.select("div.file_list > ul > li")
                                    .append("<br/><br/>");
                            select.select("style").remove();
                            select.select("script").remove();
                            return select.html();
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            RichText.fromHtml(result)
                                    .clickable(true)
                                    .autoPlay(true)
                                    .imageClick((list, i) -> SimpleUtils.openUrl(mContext, list.get(i)))
                                    .into(info);
                            progressBar.setVisibility(View.GONE);
                        }, throwable -> {
                            MessageBar.create(mContext, "出現錯誤：" + throwable.getMessage());
                            dialog.dismiss();
                        })
        );
    }
}
