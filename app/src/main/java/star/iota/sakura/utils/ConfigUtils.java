package star.iota.sakura.utils;


import android.content.Context;
import android.content.SharedPreferences;


public class ConfigUtils {

    public static long getOpenCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getLong("open_count", 0);
    }

    public static void saveOpenCount(Context context, long count) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong("open_count", count);
        edit.apply();
    }

    public static boolean isShowDonation(Context context) {
        SharedPreferences sp = context.getSharedPreferences("donation_config", Context.MODE_PRIVATE);
        return sp.getBoolean("show_donation", true);
    }

    public static void saveDonationStatus(Context context, boolean show) {
        SharedPreferences sp = context.getSharedPreferences("donation_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("show_donation", show);
        edit.apply();
    }
}
