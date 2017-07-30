package star.iota.sakura.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import star.iota.sakura.utils.SnackbarUtils;


public class NetStatusBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isAvailable()) {
                if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    SnackbarUtils.create(context, "您正在使用WiFi");
                } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    SnackbarUtils.create(context, "您正在使用有线网络");
                } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    SnackbarUtils.create(context, "您正在使用流量，请注意流量情况");
                }
            } else {
                SnackbarUtils.create(context, "网络已断开，请检查网络");
            }
        }

    }
}
