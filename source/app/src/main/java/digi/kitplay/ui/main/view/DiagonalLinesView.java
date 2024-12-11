package digi.kitplay.ui.main.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import digi.kitplay.R;
import digi.kitplay.constant.Constants;

public class DiagonalLinesView extends View {
    private static final int LINE_WIDTH = 2;
    private Paint paint;
    private Path path;
    private RectF rectF;
    private boolean isCornerRadius = false;
    private float cornerRadius = 20f;
    private Bitmap cachedBitmap;
    private Canvas cachedCanvas;

    public DiagonalLinesView(Context context) {
        super(context);
        init(null);
    }

    public DiagonalLinesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DiagonalLinesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public DiagonalLinesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @SuppressLint("ResourceAsColor")
    private void init(@Nullable AttributeSet attrs) {
        paint = new Paint();
        paint.setColor(getContext().getColor(R.color.line_color));
        paint.setStrokeWidth(LINE_WIDTH);
        paint.setAntiAlias(true);

        path = new Path();
        rectF = new RectF();

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DiagonalLinesView);
            cornerRadius = a.getDimension(R.styleable.DiagonalLinesView_cornerRadius, 0);
            isCornerRadius = a.getBoolean(R.styleable.DiagonalLinesView_isCornerRadius, false);
            a.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (cachedBitmap != null) {
            cachedBitmap.recycle();
        }
        cachedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        cachedCanvas = new Canvas(cachedBitmap);
        drawContent(cachedCanvas, w, h);
    }

    private void drawContent(Canvas canvas, float width, float height) {
        if (isCornerRadius) {
            drawRoundedBackground(canvas, width, height);
        }
        drawDiagonalLines(canvas, width, height);
    }

    private void drawRoundedBackground(Canvas canvas, float width, float height) {
        rectF.set(0, 0, width, height);
        path.reset();
        path.moveTo(0, cornerRadius);
        path.quadTo(0, 0, cornerRadius, 0);
        path.lineTo(width - cornerRadius, 0);
        path.quadTo(width, 0, width, cornerRadius);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.close();
        canvas.clipPath(path);
    }

    private void drawDiagonalLines(Canvas canvas, float width, float height) {
        float circleRadius = 1.35F;
        double itemWidth = width / 24.0 * Constants.ORDER_COLUMN_COUNT;
        for (float i = 0; i < width + height; i += itemWidth) {
            float startX = Math.max(i - height, 0);
            float startY = Math.min(i, height);
            float endX = Math.min(i, width);
            float endY = Math.max(i - width, 0);
            canvas.drawLine(startX, startY, endX, endY, paint);
            canvas.drawCircle(startX, startY, circleRadius, paint);
            canvas.drawCircle(endX, endY, circleRadius, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cachedBitmap != null) {
            canvas.drawBitmap(cachedBitmap, 0, 0, null);
        }
    }
}
