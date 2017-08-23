package star.iota.sakura.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String getBefore(String timestamp) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            Date date = formatter.parse(timestamp);
            long before = date.getTime();
            long delta = System.currentTimeMillis() - before;
            long day = 24 * 60 * 60 * 1000;
            if (delta >= 3 * day) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.ENGLISH);
                return sdf.format(before);
            } else if (delta >= 2 * day) {
                SimpleDateFormat sdf = new SimpleDateFormat("前天 HH:mm", Locale.ENGLISH);
                return sdf.format(before);
            } else if (delta >= day) {
                SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm", Locale.ENGLISH);
                return sdf.format(before);
            }
            String sb = "";
            long[] times = new long[]{60 * 60 * 1000L, 60 * 1000, 1000L};
            String[] units = new String[]{"小时前", "分钟前", "秒前"};
            for (int i = 0, len = times.length; i < len; i++) {
                long time = times[i];
                if (delta < time) {
                    continue;
                }
                long temp = delta / time;
                if (temp > 0) {
                    sb = temp + units[i];
                    break;
                }
            }
            return sb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
