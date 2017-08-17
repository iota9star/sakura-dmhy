package star.iota.sakura.ui.fans.oldfans;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseViewHolder;
import star.iota.sakura.glide.GlideApp;
import star.iota.sakura.ui.fans.FanBean;


class FansViewHolder extends BaseViewHolder<FansBean> {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.image_view_week)
    ImageView mImageViewWeek;

    FansViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(FansBean bean) {
        GlideApp.with(mContext)
                .load(bean.getWeek().replace("http://", "https://"))
                .placeholder(R.drawable.bg_sakura)
                .error(R.drawable.bg_sakura)
                .fallback(R.drawable.bg_sakura)
                .into(mImageViewWeek);
        FanAdapter adapter = new FanAdapter();
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(adapter);
        List<FanBean> fans = bean.getFans();
        Iterator<FanBean> iterator = fans.iterator();
        while (iterator.hasNext()) {
            FanBean next = iterator.next();
            if (next.getName().equals("&nbsp;")) {
                iterator.remove();
            }
        }
        adapter.add(fans);
    }
}
