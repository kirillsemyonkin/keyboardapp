package kirillsemyonkin.keyboardapp;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static java.lang.Math.floor;
import static java.lang.Math.floorDiv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import kirillsemyonkin.keyboardapp.action.KeyboardKey;

public class KeyboardAppView extends View {
    public static final int KEY_CORNER_RADIUS = 15;
    public static final int KEY_PADDING = 3;
    public static final float KEY_TEXT_FACTOR = 0.4f;

    public static final Paint KEY_BACKGROUND = new Paint();
    public static final Paint KEY_BACKGROUND_DOWN = new Paint();
    public static final Paint KEY_TEXT = new Paint(ANTI_ALIAS_FLAG);

    static {
        KEY_BACKGROUND.setColor(0xFF222222);
        KEY_BACKGROUND_DOWN.setColor(0xFF111111);
        KEY_TEXT.setColor(0xFFEEEEEE);
    }

    private KeyboardService service;

    public KeyboardAppView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardAppView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public KeyboardAppView service(KeyboardService service) {
        this.service = service;
        return this;
    }

    private KeyboardKey unprojectToKey(int posX, int posY) {
        var layout = service.layout();

        // Determine row from posY and ensure valid
        var rows = layout.rowCount();
        var row = floorDiv(posY * rows, getHeight());
        if (!(row >= 0 && row < rows)) return null;

        // Determine col from posX and ensure valid
        var cols = layout.colCount(row);
        var totalGrowthFactor = layout.growthFactor();

        var totalViewWidth = getWidth();

        var widths = new int[cols];
        int totalKeysWidth = 0;
        for (var col = 0; col < cols; col++) {
            var key = layout.key(col, row);
            assert key != null;

            totalKeysWidth
                += widths[col]
                = (int) floor(totalViewWidth * key.growthFactor() / totalGrowthFactor);
        }

        for (int col = 0, x = floorDiv(totalViewWidth - totalKeysWidth, 2); col < cols; x += widths[col++])
            if (x <= posX && posX < x + widths[col])
                return layout.key(col, row);

        return null;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        var screenHeight
            = getResources()
            .getDisplayMetrics()
            .heightPixels;
        setMeasuredDimension(
            getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), // parent width
            screenHeight / 2);
    }

    protected void onDraw(Canvas canvas) {
        var layout = service.layout();
        var totalGrowthFactor = layout.growthFactor();

        var totalViewWidth = getWidth();
        var totalViewHeight = getHeight();

        var rows = layout.rowCount();
        if (rows == 0) return;
        var keyHeight = floorDiv(totalViewHeight, rows);

        // In future might be a good idea to find widest key icon
        //   and use it to make all icons always fit horizontally
        KEY_TEXT.setTextSize(keyHeight * KEY_TEXT_FACTOR);

        for (var row = 0; row < rows; row++) {
            var y = row * keyHeight;

            var cols = layout.colCount(row);
            if (cols == 0) continue;

            @SuppressLint("DrawAllocation")
            var widths = new int[cols];
            int totalKeysWidth = 0;
            for (var col = 0; col < cols; col++) {
                var key = layout.key(col, row);
                assert key != null;

                totalKeysWidth
                    += widths[col]
                    = (int) floor(totalViewWidth * key.growthFactor() / totalGrowthFactor);
            }

            for (int col = 0, x = floorDiv(totalViewWidth - totalKeysWidth, 2); col < cols; x += widths[col++]) {
                var keyWidth = widths[col];

                var key = layout.key(col, row);
                assert key != null;

                canvas.drawRoundRect(
                    x + KEY_PADDING,
                    y + KEY_PADDING,
                    x + keyWidth - KEY_PADDING * 2,
                    y + keyHeight - KEY_PADDING * 2,
                    KEY_CORNER_RADIUS,
                    KEY_CORNER_RADIUS,
                    currentlyDown == key
                        ? KEY_BACKGROUND_DOWN
                        : KEY_BACKGROUND);

                canvas.save();
                {
                    canvas.translate(
                        x + keyWidth / 2f,
                        y + keyHeight / 2f);
                    key.icon().draw(canvas);
                }
                canvas.restore();
            }
        }
    }

    private KeyboardKey currentlyDown;

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        // TODO multitouch
        var x = (int) event.getX();
        var y = (int) event.getY();

        var key = unprojectToKey(x, y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                currentlyDown = key;

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (currentlyDown == null) break;

                break;
            }
            case MotionEvent.ACTION_UP: {
                if (currentlyDown == null || currentlyDown != key) break;

                currentlyDown.action(service);
                currentlyDown = null;

                break;
            }
        }

        invalidate();
        return true;
    }
}