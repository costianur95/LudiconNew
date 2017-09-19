package larc.ludiconprod.Utils.MyProfileUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import larc.ludiconprod.R;

/**
 * Created by alex_ on 18.09.2017.
 */

public class Bar extends View {

    private static final Paint PINK = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint PINK_ALPHA = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint WHITE = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    static {
        PINK.setStyle(Paint.Style.FILL);
        PINK.setColor(0xffd4498b);
        PINK_ALPHA.setStyle(Paint.Style.FILL);
        PINK_ALPHA.setColor(0x40d4498b);
        WHITE.setStyle(Paint.Style.FILL);
        WHITE.setColor(Color.WHITE);
        WHITE.setTextAlign(Paint.Align.CENTER);
    }

    private double progress;
    private String text = "";

    public Bar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        WHITE.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, context.getResources().getDisplayMetrics()));

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Bar, 0, 0);

        try {
            this.text = a.getString(R.styleable.Bar_text);
            this.progress = a.getFloat(R.styleable.Bar_progress, 0);
        } finally {
            a.recycle();
        }
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int w = super.getWidth();
        final int h = super.getHeight();

        int roundStart = w;
        int hrs = roundStart / 2;

        canvas.drawArc(new RectF(0, h - roundStart, w, h), 0, 180, true, Bar.PINK_ALPHA);
        canvas.drawRect(0, hrs, w, h - hrs, Bar.PINK_ALPHA);
        canvas.drawArc(new RectF(0, 0, w, roundStart), 180, 180, true, Bar.PINK_ALPHA);


        if (this.progress > 0) {
            int up = (int) (hrs + (h - roundStart) * (1 - this.progress / 100d));

            canvas.drawArc(new RectF(0, h - roundStart, w, h), 0, 180, true, Bar.PINK);
            canvas.drawRect(0, up, w, h - hrs, Bar.PINK);
            canvas.drawArc(new RectF(0, up - hrs, w, up + hrs), 180, 180, true, Bar.PINK);
        }

        float t = ((WHITE.descent() + WHITE.ascent()) / 2);
        canvas.drawText(this.text, (float) (w / 2), (float) (h - WHITE.getTextSize() * 1.5), WHITE);
    }
}
