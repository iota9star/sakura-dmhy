package star.iota.sakura.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import star.iota.sakura.R;
import star.iota.sakura.Url;
import star.iota.sakura.base.BaseFragment;
import star.iota.sakura.glide.GlideApp;

public class AboutFragment extends BaseFragment {
    @BindView(R.id.image_view_banner)
    ImageView mImageViewBanner;
    @BindView(R.id.text_view_thx)
    TextView mTextViewTHX;

    @OnClick({R.id.text_view_dmhy, R.id.linear_layout_grade})
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (view.getId()) {
            case R.id.text_view_dmhy:
                intent.setData(Uri.parse(Url.BASE));
                break;
            case R.id.linear_layout_grade:
                intent.setData(Uri.parse("market://details?id=" + mContext.getPackageName()));
                break;
        }
        if (intent.getData() != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void init() {
        setToolbarTitle("關於本軟");
        GlideApp.with(mContext)
                .load(getResources().getString(R.string.sakura))
                .into(mImageViewBanner);
        mTextViewTHX.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

}
