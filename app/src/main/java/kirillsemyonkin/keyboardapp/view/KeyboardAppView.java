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
import android.view.ViewConfiguration;

import java.util.HashMap;
import java.util.Map;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.action.AltCharAppendKey;
import kirillsemyonkin.keyboardapp.action.KeyboardKey;
import kirillsemyonkin.keyboardapp.util.AltMenu;
import kirillsemyonkin.keyboardapp.util.KeyProjection;

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
        var rowNum = floorDiv(posY * rows, getHeight());
        var row = layout.row(rowNum);
        if (row == null) return null;

        // Determine col from posX and ensure valid
        var cols = row.colCount();
        var totalGrowthFactor = layout.growthFactor();

        var totalViewWidth = getWidth();

        var rowGrowthFactor = row.growthFactor();
        if (rowGrowthFactor <= 0) rowGrowthFactor = totalGrowthFactor;

        var widths = new int[cols];
        int totalKeysWidth = 0;
        for (var colNum = 0; colNum < cols; colNum++) {
            var key = row.key(colNum);
            assert key != null;

            totalKeysWidth
                += widths[colNum]
                = (int) floor(totalViewWidth * key.growthFactor() / rowGrowthFactor);
        }

        for (int colNum = 0, x = floorDiv(totalViewWidth - totalKeysWidth, 2); colNum < cols; x += widths[colNum++])
            if (x <= posX && posX < x + widths[colNum])
                return row.key(colNum);

        return null;
    }

    private KeyProjection projectFromKey(KeyboardKey key) {
        var layout = service.layout();
        var totalGrowthFactor = layout.growthFactor();

        var totalViewWidth = getWidth();
        var totalViewHeight = getHeight();

        var rows = layout.rowCount();
        if (rows == 0) return null;
        var keyHeight = floorDiv(totalViewHeight, rows);

        for (var rowNum = 0; rowNum < rows; rowNum++) {
            var row = layout.row(rowNum);
            assert row != null;

            var cols = row.colCount();
            if (cols == 0) continue;

            var rowGrowthFactor = row.growthFactor();
            if (rowGrowthFactor <= 0) rowGrowthFactor = totalGrowthFactor;

            var widths = new int[cols];
            int totalKeysWidth = 0;
            for (var colNum = 0; colNum < cols; colNum++) {
                var k = row.key(colNum);
                assert k != null;

                totalKeysWidth
                    += widths[colNum]
                    = (int) floor(totalViewWidth * k.growthFactor() / rowGrowthFactor);
            }

            for (int colNum = 0, x = floorDiv(totalViewWidth - totalKeysWidth, 2); colNum < cols; x += widths[colNum++])
                if (key == row.key(colNum))
                    return new KeyProjection(x, rowNum * keyHeight, widths[colNum]);
        }

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
        //   and use it to make all icons always fit both dimensions
        KEY_TEXT.setTextSize(keyHeight * KEY_TEXT_FACTOR);

        // Draw keys
        for (var rowNum = 0; rowNum < rows; rowNum++) {
            var row = layout.row(rowNum);
            assert row != null;

            var y = rowNum * keyHeight;

            var cols = row.colCount();
            if (cols == 0) continue;

            var rowGrowthFactor = row.growthFactor();
            if (rowGrowthFactor <= 0) rowGrowthFactor = totalGrowthFactor;

            @SuppressLint("DrawAllocation")
            var widths = new int[cols];
            int totalKeysWidth = 0;
            for (var colNum = 0; colNum < cols; colNum++) {
                var key = row.key(colNum);
                assert key != null;

                totalKeysWidth
                    += widths[colNum]
                    = (int) floor(totalViewWidth * key.growthFactor() / rowGrowthFactor);
            }

            for (int colNum = 0, x = floorDiv(totalViewWidth - totalKeysWidth, 2); colNum < cols; x += widths[colNum++]) {
                var keyWidth = widths[colNum];

                var key = row.key(colNum);
                assert key != null;

                canvas.drawRoundRect(
                    x + KEY_PADDING,
                    y + KEY_PADDING,
                    x + keyWidth - KEY_PADDING,
                    y + keyHeight - KEY_PADDING,
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

        // Draw alt chars menu
        if (currentAltMenu != null)
            currentAltMenu.draw(canvas, keyHeight);
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
                    .put(pointerID, unprojectToKey(x, y));

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
                var actionKey = unprojectToKey(x, y);

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

            var totalGrowthFactor = layout.growthFactor();
            var altKeyWidth = (int) floor(totalViewWidth / totalGrowthFactor);
            var totalMenuWidth = altKeyWidth * (1 + key.altChars().length);

            var projection = projectFromKey(key);
            assert projection != null;

            var pressedKeyMiddle = projection.x() + floorDiv(projection.width(), 2);
            var halfAltKeyWidth = floorDiv(altKeyWidth, 2);
            var ifLeftBorderStart = pressedKeyMiddle - halfAltKeyWidth;

            var borderX = ifLeftBorderStart;
            var rightBorderFirst = false;
            // Ensure alt chars menu fits, else mirror
            if (ifLeftBorderStart + totalMenuWidth > totalViewWidth) {
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
                altKeyWidth);
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