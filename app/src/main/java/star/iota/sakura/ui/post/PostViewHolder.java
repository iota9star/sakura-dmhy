package star.iota.sakura.ui.post;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.base.BaseViewHolder;
import star.iota.sakura.ui.more.MoreActivity;
import star.iota.sakura.ui.web.WebActivity;
import star.iota.sakura.utils.SnackbarUtils;


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
    @BindView(R.id.card_view_container)
    CardView mCardView;

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
                        SnackbarUtils.create(mContext, "該發佈者無鏈接");
                    }
                });
            }
        }
        mButtonMagnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copy(bean.getMagnet());
                SnackbarUtils.create(mContext, "內容已複製到剪切板");
            }
        });
        mButtonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", bean.getUrl());
                mContext.startActivity(intent);
            }
        });
    }

    private void copy(String url) {
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("image_url", url));
    }
}
