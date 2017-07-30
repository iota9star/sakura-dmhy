package star.iota.sakura.base;


import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import star.iota.sakura.broadcast.NetStatusBroadcastReceiver;

public abstract class BaseActivity extends AppCompatActivity {


    protected Context mContext;
    private Unbinder unbinder;
    private NetStatusBroadcastReceiver mNetStatusBroadcastReceiver;

    private void initNetBroadcastReceiver() {
        mNetStatusBroadcastReceiver = new NetStatusBroadcastReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetStatusBroadcastReceiver, mFilter);
    }

    protected abstract void init();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        mContext = this;
        unbinder = ButterKnife.bind(this);
        initNetBroadcastReceiver();
        init();
        setFirstFragment();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (mNetStatusBroadcastReceiver != null) {
            unregisterReceiver(mNetStatusBroadcastReceiver);
        }
    }

    protected abstract int getContentViewId();

    protected int getFragmentContainerId() {
        return 0;
    }

    protected void setFirstFragment() {

    }

    protected void showFragment(BaseFragment fragment) {
        if (fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction tx = fm.beginTransaction();
            tx.replace(getFragmentContainerId(), fragment);
            tx.commit();
        }
    }

    public void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(getFragmentContainerId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    protected void exit() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "真的要退出了吗", Snackbar.LENGTH_LONG)
                    .setAction("EXIT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.exit(0);
                        }
                    })
                    .show();
        }
    }

    protected void removeFragmentContainerChildrenViews() {
        ((ViewGroup) findViewById(getFragmentContainerId())).removeAllViews();
    }
}
