package star.iota.sakura.ui.fans.newfans;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseViewHolder;
import star.iota.sakura.glide.GlideApp;


public class NewFansViewHolder extends BaseViewHolder<NewFansBean> {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.image_view_week)
    ImageView mImageViewWeek;

    NewFansViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(NewFansBean bean) {
        GlideApp.with(mContext)
                .load(bean.getWeek().replace("http://", "https://"))
                .placeholder(R.drawable.bg_sakura)
                .error(R.drawable.bg_sakura)
                .fallback(R.drawable.bg_sakura)
                .into(mImageViewWeek);
        NewFanAdapter adapter = new NewFanAdapter();
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(adapter);
        adapter.add(bean.getFans());
    }
}
