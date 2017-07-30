package star.iota.sakura.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;


public class SnackbarUtils {
    public static void create(Context context, String content) {
        int[] faces = {
                0x1F43D, 0x1F620, 0x1F629, 0x1F632,
                0x1F61E, 0x1F635, 0x1F630, 0x1F612,
                0x1F60D, 0x1F624, 0x1F61C, 0x1F61D,
                0x1F60B, 0x1F618, 0x1F61A, 0x1F637,
                0x1F633, 0x1F603, 0x1F605, 0x1F606,
                0x1F601, 0x1F602, 0x1F60A, 0x263A,
                0x1F604, 0x1F622, 0x1F62D, 0x1F628,
                0x1F623, 0x1F621, 0x1F60C, 0x1F616,
                0x1F614, 0x1F631, 0x1F62A, 0x1F60F,
                0x1F613, 0x1F625, 0x1F62B, 0x1F609,
                0x1F63A, 0x1F638, 0x1F639, 0x1F63D,
                0x1F63B, 0x1F63F, 0x1F63E, 0x1F63C,
                0x1F640, 0x1F47B, 0x1F47C, 0x1F47F,
                0x1F47E, 0x1F419, 0x1F42F, 0x1F42C,
                0x1F438, 0x1F43C
        };
        final Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), content, Snackbar.LENGTH_LONG);
        snackbar.setAction(new String(Character.toChars(faces[(int) (Math.random() * faces.length)])), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

}
