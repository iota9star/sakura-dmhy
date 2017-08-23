package star.iota.sakura.ui.post;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.base.BaseViewHolder;
import star.iota.sakura.database.SubsDAO;
import star.iota.sakura.database.SubsDAOImpl;
import star.iota.sakura.ui.more.MoreActivity;
import star.iota.sakura.utils.MessageBar;
import star.iota.sakura.utils.SimpleUtils;


class PostViewHolder extends BaseViewHolder<PostBean> {
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
    }

    @Override
    public void bindView(final PostBean bean) {
        mTextViewCategory.setText(bean.getCategory());
        mTextViewTitle.setText(bean.getTitle());
        mTextViewDate.setText(bean.getDate());
        mTextViewSize.setText(bean.getSize());
        if (mContext instanceof MoreActivity) {
            mButtonSub.setVisibility(View.GONE);
            mButtonSub.setOnClickListener(null);
        } else {
            mButtonSub.setVisibility(View.VISIBLE);
            if (bean.getSub() != null) {
                mButtonSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((BaseActivity) mContext).addFragment(
                                PostFragment.newInstance(bean.getSub().getUrl() + "/page/",
                                        "",
                                        bean.getSub().getName()));
                    }
                });
            } else {
                mButtonSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MessageBar.create(mContext, "該發佈者無鏈接");
                    }
                });
            }
        }
        mButtonMagnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleUtils.copy(mContext, bean.getMagnet());
            }
        });
        mButtonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleUtils.openUrl(mContext, bean.getUrl());
            }
        });
        mButtonCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Observable.just(new SubsDAOImpl(mContext))
                        .filter(new Predicate<SubsDAO>() {
                            @Override
                            public boolean test(@NonNull SubsDAO subsDAO) throws Exception {
                                boolean exist = subsDAO.exist(bean.getUrl());
                                if (exist) {
                                    MessageBar.create(mContext, "已存在：" + bean.getTitle());
                                }
                                return !exist;
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .map(new Function<SubsDAO, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull SubsDAO subsDAO) throws Exception {
                                return subsDAO.save(bean);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    MessageBar.create(mContext, "收藏成功：" + bean.getTitle());
                                } else {
                                    MessageBar.create(mContext, "收藏過程中可能出現錯誤");
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                MessageBar.create(mContext, "收藏過程中可能出現錯誤：" + throwable.getMessage());
                            }
                        });
            }
        });
    }
}
