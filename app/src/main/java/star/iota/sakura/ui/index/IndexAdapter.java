package star.iota.sakura.ui.index;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.lankton.flowlayout.FlowLayout;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.ui.fans.oldfans.FansFragment;


class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.IndexViewHolder> {
    private final List<IndexBean.YearBean> mBeans;
    private final LayoutInflater mInflater;
    private final BaseActivity mActivity;

    IndexAdapter(BaseActivity activity) {
        this.mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mBeans = new ArrayList<>();
    }

    @Override
    public IndexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IndexViewHolder(mInflater.inflate(R.layout.item_index, parent, false));
    }

    @Override
    public void onBindViewHolder(IndexViewHolder holder, int position) {
        final IndexBean.YearBean bean = mBeans.get(position);
        holder.mTextViewYear.setText(String.valueOf(bean.getYear()));
        holder.mFlowLayout.removeAllViews();
        holder.mFlowLayout.relayoutToAlign();
        for (final IndexBean.YearBean.SeasonBean season : bean.getSeasons()) {
            ImageView tag = (ImageView) mInflater.inflate(R.layout.item_index_season, holder.mFlowLayout, false);
            if (season.getText().contains("春")) {
                tag.setImageResource(R.drawable.ic_spring);
            } else if (season.getText().contains("夏")) {
                tag.setImageResource(R.drawable.ic_summer);
            } else if (season.getText().contains("秋")) {
                tag.setImageResource(R.drawable.ic_autumn);
            } else if (season.getText().contains("冬")) {
                tag.setImageResource(R.drawable.ic_winter);
            }
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.addFragment(FansFragment.newInstance(season.getIndex()));
                }
            });
            holder.mFlowLayout.addView(tag);
        }
    }

    public void add(List<IndexBean.YearBean> beans) {
        mBeans.addAll(beans);
        notifyItemRangeInserted(0, beans.size());
    }

    public void clear() {
        int size = mBeans.size();
        mBeans.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    static class IndexViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_year)
        TextView mTextViewYear;
        @BindView(R.id.flow_layout)
        FlowLayout mFlowLayout;

        IndexViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
