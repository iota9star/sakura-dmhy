package star.iota.sakura.base;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class BaseAdapter<V extends RecyclerView.ViewHolder, B> extends RecyclerView.Adapter {

    protected final List<B> mBeans;

    protected BaseAdapter() {
        mBeans = new ArrayList<>();
    }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    public void add(List<B> beans) {
        int size = mBeans.size();
        mBeans.addAll(beans);
        notifyItemRangeInserted(size, beans.size());
    }


    public void clear() {
        int size = mBeans.size();
        mBeans.clear();
        notifyItemRangeRemoved(0, size);
    }
}
