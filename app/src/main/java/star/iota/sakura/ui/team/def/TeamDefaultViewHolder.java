package star.iota.sakura.ui.team.def;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import star.iota.sakura.R;
import star.iota.sakura.Url;
import star.iota.sakura.base.BaseActivity;
import star.iota.sakura.base.BaseViewHolder;
import star.iota.sakura.ui.post.PostFragment;
import star.iota.sakura.ui.team.TeamBean;


class TeamDefaultViewHolder extends BaseViewHolder<TeamBean> {
    @BindView(R.id.card_view_container)
    CardView mCardViewContainer;
    @BindView(R.id.text_view_team_name)
    TextView mTextViewTeamName;

    TeamDefaultViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(final TeamBean bean) {
        mTextViewTeamName.setText(bean.getName());
        mCardViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) mContext).addFragment(
                        PostFragment.newInstance(Url.TEAM_DEFAULT + bean.getId() + "/page/",
                                "",
                                bean.getName()));
            }
        });
    }
}
