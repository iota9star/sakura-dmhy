package star.iota.sakura.ui.local.subs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import star.iota.sakura.R;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.database.SubsDAO;
import star.iota.sakura.database.SubsDAOImpl;
import star.iota.sakura.ui.post.PostBean;
import star.iota.sakura.ui.post.PostFragment;
import star.iota.sakura.utils.MessageBar;
import star.iota.sakura.utils.SimpleUtils;


class LocalSubsAdapter extends RecyclerView.Adapter<LocalSubsAdapter.MyViewHolder> {

    private final List<PostBean> list;

    LocalSubsAdapter() {
        list = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_subs, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int pos) {
        final PostBean bean = list.get(pos);
        holder.mTextViewCategory.setText(bean.getCategory());
        holder.mTextViewTitle.setText(bean.getTitle());
        holder.mTextViewDate.setText(bean.getDate());
        holder.mTextViewSize.setText(bean.getSize());
        if (bean.getSub() != null) {
            holder.mButtonSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((BaseActivity) holder.context).addFragment(
                            PostFragment.newInstance(bean.getSub().getUrl() + "/page/",
                                    "",
                                    bean.getSub().getName()));
                }
            });
        } else {
            holder.mButtonSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageBar.create(holder.context, "該發佈者無鏈接");
                }
            });
        }
        holder.mButtonMagnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleUtils.copy(holder.context, bean.getMagnet());
            }
        });
        holder.mButtonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleUtils.openUrl(holder.context, bean.getUrl());
            }
        });
        holder.mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Observable.just(new SubsDAOImpl(holder.context))
                        .map(new Function<SubsDAO, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull SubsDAO subsDAO) throws Exception {
                                return subsDAO.delete(bean.getId());
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    MessageBar.create(holder.context, "刪除成功：" + bean.getTitle());
                                    remove(holder.getAdapterPosition());
                                } else {
                                    MessageBar.create(holder.context, "刪除過程可能出現錯誤");
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                MessageBar.create(holder.context, "刪除過程可能出現錯誤：" + throwable.getMessage());
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(List<PostBean> beans) {
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
        @BindView(R.id.button_delete)
        Button mButtonDelete;

        MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }
    }
}
