package kirillsemyonkin.keyboardapp.view;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static java.lang.Math.floor;
import static java.lang.Math.floorDiv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.action.KeyboardKey;

public class KeyboardAppView extends View {
    public static final int KEY_CORNER_RADIUS = 15;
    public static final int KEY_PADDING = 3;
    public static final float KEY_TEXT_FACTOR = 0.4f;

    public static final Paint KEY_BACKGROUND = new Paint();
    public static final Paint KEY_BACKGROUND_HIGHLIGHT = new Paint();
    public static final Paint KEY_BACKGROUND_DOWN = new Paint();
    public static final Paint KEY_TEXT = new Paint(ANTI_ALIAS_FLAG);

    static {
        KEY_BACKGROUND.setColor(0xFF222222);
        KEY_BACKGROUND_HIGHLIGHT.setColor(0xFF3366FF);
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
                    currentlyDownPointers.containsValue(key)
                        ? KEY_BACKGROUND_DOWN
                        : (key.highlight()
                        ? KEY_BACKGROUND_HIGHLIGHT
                        : KEY_BACKGROUND));

                canvas.save();
                {
                    canvas.translate(
                        x + keyWidth / 2f,
                        y + keyHeight / 2f);
                    key.icon().draw(canvas, getResources());
                }
                canvas.restore();
            }
        }
    }

    private final Map<Integer, KeyboardKey> currentlyDownPointers = new HashMap<>();

    private final int LONG_PRESS_MSG_ID = 0;
    private final long LONG_PRESS_DELAY = 400; // .4s
    private final int LONG_HOLD_MSG_ID = 1;
    private final long LONG_HOLD_REPEAT = 50; // 20 repeats/sec

    private final Handler longPressHandler
        = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            // Ensure received pointer-key pair is valid
            var pointerID = (int) msg.obj;
            var currentlyDown
                = currentlyDownPointers
                .get(pointerID);
            if (currentlyDown == null) return;

            // Call `hold` and try to repeat
            currentlyDown.hold(service);
            sendMessageDelayed(
                Message.obtain(
                    this,
                    LONG_HOLD_MSG_ID,
                    pointerID),
                LONG_HOLD_REPEAT);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        var pointerIndex = event.getActionIndex();
        var pointerID = event.getPointerId(pointerIndex);

        var x = (int) event.getX(pointerIndex);
        var y = (int) event.getY(pointerIndex);
        var eventKey = unprojectToKey(x, y);

        switch (event.getActionMasked()) {
            // Prepare for tap and long hold
            case ACTION_DOWN: {
                currentlyDownPointers
                    .put(pointerID, eventKey);

                longPressHandler
                    .sendMessageDelayed(
                        Message.obtain(
                            longPressHandler,
                            LONG_PRESS_MSG_ID,
                            pointerID),
                        LONG_PRESS_DELAY);

                break;
            }

            case ACTION_MOVE: {
                break;
            }

            // Tap and stop long hold
            case ACTION_UP: {
                var currentlyDown
                    = currentlyDownPointers
                    .get(pointerID);
                if (currentlyDown == null) break;

                // Call `tap` if releasing on same key as pressed
                //   and it has not been long-held yet
                if (currentlyDown == eventKey
                    && !longPressHandler
                    .hasMessages(
                        LONG_HOLD_MSG_ID,
                        pointerID))
                    currentlyDown.tap(service);

                // Cancel long delay and repeat
                longPressHandler
                    .removeMessages(
                        LONG_PRESS_MSG_ID,
                        pointerID);
                longPressHandler
                    .removeMessages(
                        LONG_HOLD_MSG_ID,
                        pointerID);

                currentlyDownPointers
                    .remove(pointerID);

                break;
            }
        }

        invalidate();
        return true;
    }
}