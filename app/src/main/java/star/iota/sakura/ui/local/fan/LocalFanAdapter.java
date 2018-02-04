package star.iota.sakura.ui.local.fan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.lankton.flowlayout.FlowLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.blurry.Blurry;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.database.FanDAOImpl;
import star.iota.sakura.glide.GlideApp;
import star.iota.sakura.ui.fans.FanBean;
import star.iota.sakura.ui.fans.SubBean;
import star.iota.sakura.ui.more.MoreActivity;
import star.iota.sakura.ui.post.PostFragment;
import star.iota.sakura.utils.MessageBar;

class LocalFanAdapter extends RecyclerView.Adapter<LocalFanAdapter.MyViewHolder> {

    private final List<FanBean> list;

    LocalFanAdapter() {
        list = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_fan, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int pos) {
        final FanBean bean = list.get(pos);
        LayoutInflater inflater = LayoutInflater.from(holder.context);
        holder.mFlowLayout.relayoutToCompress();
        holder.mFlowLayout.removeAllViews();
        for (final SubBean sub : bean.getSubs()) {
            TextView b = (TextView) inflater.inflate(R.layout.item_sub, holder.mFlowLayout, false);
            b.setText(sub.getName());
            b.setOnClickListener(view -> ((BaseActivity) holder.context).addFragment(
                    PostFragment.newInstance(sub.getUrl() + "/page/",
                            "",
                            sub.getName())));
            holder.mFlowLayout.addView(b);
        }
        GlideApp.with(holder.context)
                .asBitmap()
                .load(bean.getCover().replace("http://", "https://"))
                .placeholder(R.drawable.bg_sakura)
                .fallback(R.drawable.bg_sakura)
                .error(R.drawable.bg_sakura)
                .dontAnimate()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                        Blurry.with(holder.context).from(bitmap).into(holder.mImageViewBanner);
                        return false;
                    }
                })
                .into(holder.mImageViewCover);
        holder.mTextViewName.setText(bean.getName());
        holder.mTextViewOfficial.setText(String.format("官網：%s", bean.getOfficial()));
        holder.mTextViewIndex.setText(String.valueOf(list.size() - pos));
        holder.mImageViewCover.setOnClickListener(view -> {
            Intent intent = new Intent(holder.context, MoreActivity.class);
            intent.putExtra("bean", bean);
            holder.context.startActivity(intent);
        });
        holder.mCardView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(holder.context)
                    .setIcon(R.mipmap.app_icon)
                    .setTitle("是否删除 - " + bean.getName())
                    .setNegativeButton("嗯", (dialogInterface, i) -> delete(holder, bean))
                    .setPositiveButton("只是看看", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
            return false;
        });
    }

    private void delete(final MyViewHolder holder, final FanBean bean) {
        Observable.just(new FanDAOImpl(holder.context))
                .map(fanDAO -> fanDAO.delete(bean))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        MessageBar.create(holder.context, "刪除成功：" + bean.getName());
                        remove(holder.getAdapterPosition());
                    } else {
                        MessageBar.create(holder.context, "刪除錯誤：" + bean.getName());
                    }
                }, throwable -> MessageBar.create(holder.context, "刪除錯誤：" + throwable.getMessage()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(List<FanBean> beans) {
        int size = list.size();
        list.addAll(beans);
        notifyItemRangeInserted(size, beans.size());
    }

    private void remove(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);
    }

    public void clear() {
        int size = list.size();
        list.clear();
        notifyItemRangeRemoved(0, size);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        @BindView(R.id.text_view_name)
        TextView mTextViewName;
        @BindView(R.id.text_view_official)
        TextView mTextViewOfficial;
        @BindView(R.id.flow_layout)
        FlowLayout mFlowLayout;
        @BindView(R.id.image_view_cover)
        ImageView mImageViewCover;
        @BindView(R.id.image_view_banner)
        ImageView mImageViewBanner;
        @BindView(R.id.text_view_index)
        TextView mTextViewIndex;
        @BindView(R.id.card_view_container)
        CardView mCardView;

        MyViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }
    }
}
