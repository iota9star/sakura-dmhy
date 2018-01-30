package star.iota.sakura.ui.rss;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.callback.OnImageClickListener;

import java.util.List;

import butterknife.BindView;
import jp.wasabeef.blurry.Blurry;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseViewHolder;
import star.iota.sakura.glide.GlideApp;
import star.iota.sakura.utils.DateUtils;
import star.iota.sakura.utils.SimpleUtils;


public class WithCoverViewHolder extends BaseViewHolder<RSSPostBean> {

    @BindView(R.id.image_view_banner)
    ImageView imageViewBanner;
    @BindView(R.id.image_view_cover)
    ImageView imageViewCover;
    @BindView(R.id.text_view_category)
    TextView textViewCategory;
    @BindView(R.id.text_view_date)
    TextView textViewDate;
    @BindView(R.id.card_view_container)
    CardView cardViewContainer;
    @BindView(R.id.button_magnet)
    Button buttonMagnet;
    @BindView(R.id.text_view_title)
    TextView textViewTitle;
    @BindView(R.id.button_link)
    Button buttonLink;
    @BindView(R.id.card_view_cover_container)
    CardView cardViewCoverContainer;


    WithCoverViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(final RSSPostBean bean) {
        textViewTitle.setText(("/" + bean.getTitle().replaceAll("]\\s*\\[|\\[|]|】\\s*【|】|【", "/") + "/").replaceAll("(/\\s*/+)+", "/"));
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
        cardViewCoverContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleUtils.openUrl(mContext, bean.getCover());
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
                .load(bean.getCover())
                .placeholder(R.drawable.bg_sakura)
                .error(R.drawable.bg_sakura)
                .fallback(R.drawable.bg_sakura)
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>(mContext.getResources().getDimensionPixelSize(R.dimen.v96dp), mContext.getResources().getDimensionPixelSize(R.dimen.v128dp)) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Blurry.with(mContext).from(resource).into(imageViewBanner);
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                List<Palette.Swatch> swatches = palette.getSwatches();
                                for (Palette.Swatch swatch : swatches) {
                                    if (swatch != null) {
                                        bindTextColor(swatch);
                                    }
                                }
                            }
                        });
                        imageViewCover.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        GlideApp.with(mContext).clear(imageViewCover);
                        String cover = bean.getCover().replace("http:", "https:");
                        bean.setCover(cover);
                        onErrorLoadDefault();
                    }
                });
    }

    private void showInfo(final RSSPostBean bean) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_info, null);
        TextView info = view.findViewById(R.id.text_view_info);
        RichText.fromHtml(bean.getDescription())
                .clickable(true)
                .autoPlay(true)
                .imageClick(new OnImageClickListener() {
                    @Override
                    public void imageClicked(List<String> list, int i) {
                        SimpleUtils.openUrl(mContext, list.get(i));
                    }
                })
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

    private void onErrorLoadDefault() {
        GlideApp.with(mContext)
                .asBitmap()
                .load(mContext.getString(R.string.banner))
                .centerCrop()
                .placeholder(R.drawable.bg_sakura)
                .error(R.drawable.bg_sakura)
                .fallback(R.drawable.bg_sakura)
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>(mContext.getResources().getDimensionPixelSize(R.dimen.v96dp), mContext.getResources().getDimensionPixelSize(R.dimen.v128dp)) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Blurry.with(mContext).from(resource).into(imageViewBanner);
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                List<Palette.Swatch> swatches = palette.getSwatches();
                                for (Palette.Swatch swatch : swatches) {
                                    if (swatch != null) {
                                        bindTextColor(swatch);
                                    }
                                }
                            }
                        });
                        imageViewCover.setImageBitmap(resource);
                    }
                });
    }

    private void bindTextColor(Palette.Swatch swatch) {
        textViewTitle.setTextColor(swatch.getTitleTextColor());
        textViewDate.setTextColor(swatch.getTitleTextColor());
        textViewCategory.setTextColor(swatch.getBodyTextColor());
    }
}
