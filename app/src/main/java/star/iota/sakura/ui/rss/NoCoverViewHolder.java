package star.iota.sakura.ui.rss;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zzhoujay.richtext.RichText;

import butterknife.BindView;
import jp.wasabeef.blurry.Blurry;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseViewHolder;
import star.iota.sakura.glide.GlideApp;
import star.iota.sakura.utils.DateUtils;
import star.iota.sakura.utils.SimpleUtils;


public class NoCoverViewHolder extends BaseViewHolder<RSSPostBean> {

    @BindView(R.id.image_view_banner)
    ImageView imageViewBanner;
    @BindView(R.id.text_view_title)
    TextView textViewTitle;
    @BindView(R.id.text_view_category)
    TextView textViewCategory;
    @BindView(R.id.text_view_date)
    TextView textViewDate;
    @BindView(R.id.button_link)
    Button buttonLink;
    @BindView(R.id.button_magnet)
    Button buttonMagnet;
    @BindView(R.id.card_view_container)
    CardView cardViewContainer;

    NoCoverViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(final RSSPostBean bean) {
        textViewTitle.setText(bean.getTitle());
        textViewCategory.setText(bean.getCategory());
        textViewDate.setText(DateUtils.getBefore(bean.getPubDate()));
        buttonMagnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleUtils.copy(mContext, bean.getUrl());
            }
        });
        buttonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleUtils.openUrl(mContext, bean.getLink().replace("http:", "https:"));
            }
        });
        cardViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfo(bean);
            }
        });
        GlideApp.with(mContext)
                .asBitmap()
                .load(mContext.getString(R.string.banner))
                .centerCrop()
                .placeholder(R.drawable.bg_sakura)
                .fallback(R.drawable.bg_sakura)
                .error(R.drawable.bg_sakura)
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        Blurry.with(mContext).from(bitmap).into(imageViewBanner);
                    }
                });
    }

    private void showInfo(final RSSPostBean bean) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_info, null);
        TextView info = view.findViewById(R.id.text_view_info);
        RichText.fromHtml(bean.getDescription())
                .clickable(true)
                .into(info);
        new AlertDialog.Builder(mContext)
                .setView(view)
                .setTitle(mContext.getString(R.string.app_name))
                .setIcon(R.mipmap.app_icon)
                .setNegativeButton("瀏覽器打開", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SimpleUtils.openUrl(mContext, bean.getLink().replace("http:", "https:"));
                    }
                })
                .show();
    }
}
