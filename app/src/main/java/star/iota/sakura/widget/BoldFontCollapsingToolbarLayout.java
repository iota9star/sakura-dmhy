package star.iota.sakura.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;


public class BoldFontCollapsingToolbarLayout extends CollapsingToolbarLayout {
    public BoldFontCollapsingToolbarLayout(Context context) {
        super(context);
        init();
    }


    public BoldFontCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoldFontCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setExpandedTitleTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
    }
}
