package star.iota.sakura.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;


public abstract class BaseViewHolder<B> extends RecyclerView.ViewHolder {
    protected final Context mContext;

    protected BaseViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public abstract void bindView(B beans);
}
