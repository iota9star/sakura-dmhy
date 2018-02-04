package star.iota.sakura.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rubensousa.floatingtoolbar.FloatingToolbar;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import star.iota.sakura.R;
import star.iota.sakura.ui.main.MainActivity;

public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    private View mContainerView;
    private Unbinder unbinder;
    private CharSequence mPreTitle;

    protected abstract void init();

    protected abstract int getLayoutId();

    protected void setToolbarTitle(CharSequence title) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity instanceof MainActivity) {
            ((MainActivity) activity).getCollapsingToolbarLayout().setTitle(title);
        }
    }

    protected boolean isHideFab() {
        return true;
    }

    protected FloatingActionButton getFab() {
        //noinspection ConstantConditions
        return ButterKnife.findById(getActivity(), R.id.floating_action_button);
    }

    protected FloatingToolbar getFloatingToolbar() {
        //noinspection ConstantConditions
        return ButterKnife.findById(getActivity(), R.id.floating_toolbar);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContainerView == null) {
            mContainerView = inflater.inflate(getLayoutId(), container, false);
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mContext = activity;
        if (activity instanceof MainActivity) {
            mPreTitle = ((MainActivity) activity).getCollapsingToolbarLayout().getTitle();
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
            setToolbarTitle(mPreTitle);
        }
    }

}
