package star.iota.sakura.ui.fans.newfans;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseViewHolder;
import star.iota.sakura.glide.GlideApp;
import star.iota.sakura.ui.fans.FanBean;
import star.iota.sakura.ui.more.MoreActivity;


class NewFanViewHolder extends BaseViewHolder<FanBean> {

    @BindView(R.id.card_view_container)
    CardView mCardViewContainer;
    @BindView(R.id.text_view_name)
    TextView mTextViewName;
    @BindView(R.id.image_view_cover)
    ImageView mImageViewCover;

    NewFanViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(final FanBean bean) {
        GlideApp.with(mContext)
                .load(bean.getCover().replace("http://", "https://"))
                .placeholder(R.drawable.bg_sakura)
                .fallback(R.drawable.bg_sakura)
                .error(R.drawable.bg_sakura)
                .into(mImageViewCover);
        mTextViewName.setText(bean.getName());
        mCardViewContainer.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MoreActivity.class);
            intent.putExtra("bean", bean);
            mContext.startActivity(intent);
        });
    }
}
