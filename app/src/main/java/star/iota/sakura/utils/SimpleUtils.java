package star.iota.sakura.utils;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SimpleUtils {
    public static void copy(Context context, String content) {
        try {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            //noinspection ConstantConditions
            cm.setPrimaryClip(ClipData.newPlainText("content", content));
            MessageBar.create(context, "內容已複製到剪切板，同时尝试打开磁链");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(content));
            context.startActivity(intent);
        } catch (Exception e) {
            MessageBar.create(context, "發生錯誤：" + e.getMessage());
        }
    }

    public static void openUrl(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            MessageBar.create(context, "發生錯誤：" + e.getMessage());
        }
    }
}
