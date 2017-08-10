package star.iota.sakura.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import star.iota.sakura.R;

public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    private View mContainerView;
    private Unbinder unbinder;
    private CharSequence mPreTitle;

    protected abstract void init();

    protected abstract int getLayoutId();

    protected void setTitle(CharSequence title) {
        getActivity().setTitle(title);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mPreTitle = getActivity().getTitle();
    }

    protected boolean isHideFab() {
        return true;
    }

    protected FloatingActionButton getFab() {
        return ButterKnife.findById(getActivity(), R.id.floating_action_button);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContainerView == null) {
            mContainerView = inflater.inflate(getLayoutId(), container, false);
        }
        FloatingActionButton fab = getFab();
        if (isHideFab()) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
        unbinder = ButterKnife.bind(this, mContainerView);
        init();
        return mContainerView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (mPreTitle != null) {
            setTitle(mPreTitle);
        }
    }

}