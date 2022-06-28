package kirillsemyonkin.keyboardapp.view;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
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
import android.view.ViewConfiguration;

import java.util.HashMap;
import java.util.Map;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.action.AltCharAppendKey;
import kirillsemyonkin.keyboardapp.action.KeyboardKey;
import kirillsemyonkin.keyboardapp.layout.LayoutRenderer;
import kirillsemyonkin.keyboardapp.util.AltMenu;
import kirillsemyonkin.keyboardapp.util.Highlight;

public class KeyboardAppView extends View {
    public static final int KEY_CORNER_RADIUS = 15;
    public static final int KEY_PADDING = 3;
    public static final float KEY_TEXT_FACTOR = 0.4f;

    public static final Paint KEY_BACKGROUND = new Paint();
    public static final Paint KEY_BACKGROUND_HIGHLIGHT_HALF = new Paint();
    public static final Paint KEY_BACKGROUND_HIGHLIGHT = new Paint();
    public static final Paint KEY_BACKGROUND_DOWN = new Paint();
    public static final Paint KEY_TEXT = new Paint(ANTI_ALIAS_FLAG);

    static {
        KEY_BACKGROUND.setColor(0xFF222222);
        KEY_BACKGROUND_HIGHLIGHT_HALF.setColor(0xFF274499);
        KEY_BACKGROUND_HIGHLIGHT.setColor(0xFF3366FF);
        KEY_BACKGROUND_DOWN.setColor(0xFF111111);
        KEY_TEXT.setColor(0xFFEEEEEE);
    }

    private KeyboardService service;
    private LayoutRenderer renderer;

    public KeyboardAppView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardAppView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public KeyboardAppView service(KeyboardService service) {
        this.service = service;
        renderer = service.renderer();
        currentlyDownPointers.clear();
        currentAltMenu = null;
        return this;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
            getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), // parent width
            renderer.viewHeight(getResources(), getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    protected void onDraw(Canvas canvas) {
        var layout = service.layout();

        var totalViewWidth = getWidth();
        var totalViewHeight = getHeight();

        var rows = layout.rowCount();
        if (rows == 0) return;

        // Draw keys
        for (var rowNum = 0; rowNum < rows; rowNum++) {
            var row = layout.row(rowNum);
            assert row != null;

            var cols = row.colCount();
            if (cols == 0) continue;

            for (int colNum = 0; colNum < cols; colNum++) {
                var key = row.key(colNum);
                assert key != null;

                var projection = renderer.project(
                    layout,
                    totalViewWidth, totalViewHeight,
                    key);
                assert projection != null;

                // In future might be a good idea to find widest key icon
                //   and use it to make all icons always fit both dimensions
                KEY_TEXT.setTextSize(projection.height() * KEY_TEXT_FACTOR);

                // Key background
                canvas.drawRoundRect(
                    projection.x() + KEY_PADDING,
                    projection.y() + KEY_PADDING,
                    projection.x() + projection.width() - KEY_PADDING,
                    projection.y() + projection.height() - KEY_PADDING,
                    KEY_CORNER_RADIUS,
                    KEY_CORNER_RADIUS,
                    currentlyDownPointers.containsValue(key)
                        ? KEY_BACKGROUND_DOWN
                        : background(key.highlight()));

                // Key icon
                canvas.save();
                {
                    canvas.translate(
                        projection.x() + projection.width() / 2f,
                        projection.y() + projection.height() / 2f);
                    key.icon().draw(canvas, getResources());
                }
                canvas.restore();
            }
        }

        // Draw alt chars menu
        if (currentAltMenu != null)
            currentAltMenu.draw(canvas);
    }

    private Paint background(Highlight shift) {
        switch (shift) {
            case FALSE:
                return KEY_BACKGROUND;
            case TRUE:
                return KEY_BACKGROUND_HIGHLIGHT;
            default:
                switch (service.shift()) {
                    case LOWERCASE:
                        return KEY_BACKGROUND;
                    case UPPERCASE:
                        return KEY_BACKGROUND_HIGHLIGHT_HALF;
                    default:
                        return KEY_BACKGROUND_HIGHLIGHT;
                }
        }
    }

    private final Map<Integer, KeyboardKey> currentlyDownPointers = new HashMap<>();
    private AltMenu currentAltMenu;

    private final int LONG_PRESS_MSG_ID = 0;
    private final long LONG_PRESS_DELAY = ViewConfiguration.getLongPressTimeout();
    private final int LONG_HOLD_MSG_ID = 1;
    private final long LONG_HOLD_REPEAT = ViewConfiguration.getKeyRepeatDelay();

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
            currentlyDown.hold(service, pointerID);
            sendMessageDelayed(
                Message.obtain(
                    this,
                    LONG_HOLD_MSG_ID,
                    pointerID),
                LONG_HOLD_REPEAT);

            invalidate();
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        var pointerIndex = event.getActionIndex();
        var pointerID = event.getPointerId(pointerIndex);

        var x = (int) event.getX(pointerIndex);
        var y = (int) event.getY(pointerIndex);

        switch (event.getActionMasked()) {
            // Prepare for tap and long hold
            case ACTION_DOWN: {
                currentlyDownPointers
                    .put(pointerID, renderer
                        .unproject(
                            service.layout(),
                            getWidth(), getHeight(),
                            x, y));

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
                if (currentAltMenu == null
                    || !currentAltMenu.heldBy(pointerID)) break;

                currentAltMenu
                    .selectedIndex(currentAltMenu
                        .unprojectIndex(x));

                break;
            }

            // Tap and stop long hold
            case ACTION_UP: {
                var currentlyDown
                    = currentlyDownPointers
                    .get(pointerID);
                if (currentlyDown == null) break;

                var longHeld = longPressHandler
                    .hasMessages(
                        LONG_HOLD_MSG_ID,
                        pointerID);
                var actionKey = renderer
                    .unproject(
                        service.layout(),
                        getWidth(), getHeight(),
                        x, y);

                if (longHeld)
                    currentlyDown.unhold(service, pointerID);
                else if (currentlyDown == actionKey)
                    currentlyDown.tap(service);

                cancelLongPressPointer(pointerID);
                currentlyDownPointers
                    .remove(pointerID);

                break;
            }
        }

        invalidate();
        return true;
    }

    private void cancelLongPressPointer(int pointerID) {
        longPressHandler
            .removeMessages(
                LONG_PRESS_MSG_ID,
                pointerID);
        longPressHandler
            .removeMessages(
                LONG_HOLD_MSG_ID,
                pointerID);
    }

    public void openAltMenu(int pointer, AltCharAppendKey key) {
        if (currentAltMenu == null) {
            var layout = service.layout();

            var totalViewWidth = getWidth();
            var totalViewHeight = getHeight();

            var projection = renderer.project(
                layout,
                totalViewWidth, totalViewHeight,
                key);
            assert projection != null;

            var altKeyWidth = projection.width();

            var pressedKeyMiddle = projection.x() + floorDiv(projection.width(), 2);
            var halfAltKeyWidth = floorDiv(altKeyWidth, 2);

            var borderX = pressedKeyMiddle - halfAltKeyWidth;
            var rightBorderFirst = false;
            // Ensure alt chars menu fits, else mirror
            if (borderX > totalViewWidth / 2) {
                borderX = pressedKeyMiddle + halfAltKeyWidth;
                rightBorderFirst = true;
            }

            cancelLongPressPointer(pointer);
            currentAltMenu = new AltMenu(
                pointer,
                key,
                borderX,
                projection.y(),
                rightBorderFirst,
                altKeyWidth,
                projection.height());
        }
    }

    public void closeAltMenu(int pointer, AltCharAppendKey key) {
        if (currentAltMenu != null
            && currentAltMenu.heldBy(pointer, key)) {
            service.sendCharacter(currentAltMenu.selectedCharacter());
            currentAltMenu = null;
        }
    }
}