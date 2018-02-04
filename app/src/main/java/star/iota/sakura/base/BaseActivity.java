package star.iota.sakura.base;


import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import star.iota.sakura.utils.MessageBar;

public abstract class BaseActivity extends AppCompatActivity {


    private final long[] mHints = new long[2];
    protected Context mContext;
    private Unbinder unbinder;

    protected abstract void init();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        mContext = this;
        unbinder = ButterKnife.bind(this);
        init();
        setFirstFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
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
            String hi[] = {"期待与你下一次的相遇", "要经常来看看我哦", "今天也要努力呢", "与你相伴的时光使我快乐", "我是多么盼望你能早点回来"};
            System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
            mHints[mHints.length - 1] = SystemClock.uptimeMillis();
            String[] faces = MessageBar.FACES;
            Snackbar.make(findViewById(android.R.id.content), hi[new Random().nextInt(hi.length)], Snackbar.LENGTH_SHORT).setAction(faces[new Random().nextInt(faces.length)], view -> System.exit(0)).show();
            if (SystemClock.uptimeMillis() - mHints[0] <= 1600) {
                System.exit(0);
            }
        }
    }

    protected void removeFragmentContainerChildrenViews() {
        ((ViewGroup) findViewById(getFragmentContainerId())).removeAllViews();
    }
}
