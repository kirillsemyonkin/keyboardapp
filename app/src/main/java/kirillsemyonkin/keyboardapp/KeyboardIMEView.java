package kirillsemyonkin.keyboardapp;

import static android.graphics.Paint.Align.CENTER;
import static java.lang.Math.floorDiv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import kirillsemyonkin.keyboardapp.keys.KeyboardKey;
import kirillsemyonkin.keyboardapp.keys.SimpleCharAppendBackspaceKey;
import kirillsemyonkin.keyboardapp.keys.SimpleCharAppendEnterKey;
import kirillsemyonkin.keyboardapp.keys.SimpleCharAppendKey;
import kirillsemyonkin.keyboardapp.keys.SimpleCharAppendSpaceKey;

public class KeyboardIMEView extends View {
    private final int KEY_HEIGHT = 96;
    private final int KEY_WIDTH_REGULAR = 72;
    private final int KEY_CORNER_RADIUS = 10;

    private final Paint KEY_BACKGROUND = new Paint();
    private final Paint KEY_BACKGROUND_DOWN = new Paint();
    private final Paint KEY_TEXT = new Paint();

    private KeyboardIMEService service;
    private KeyboardLayout locale;

    private void init() {
        KEY_BACKGROUND.setColor(0xFF222222);
        KEY_BACKGROUND_DOWN.setColor(0xFF111111);
        KEY_TEXT.setColor(0xFFEEEEEE);
        KEY_TEXT.setTextAlign(CENTER);
        KEY_TEXT.setTextSize(30);
        locale = KeyboardLayout
            .builder()
            .row()
            .append(new SimpleCharAppendKey('q'))
            .append(new SimpleCharAppendKey('w'))
            .append(new SimpleCharAppendKey('e'))
            .append(new SimpleCharAppendKey('r'))
            .append(new SimpleCharAppendKey('t'))
            .append(new SimpleCharAppendKey('y'))
            .append(new SimpleCharAppendKey('u'))
            .append(new SimpleCharAppendKey('i'))
            .append(new SimpleCharAppendKey('o'))
            .append(new SimpleCharAppendKey('p'))
            .buildRow()
            .row()
            .append(new SimpleCharAppendKey('a'))
            .append(new SimpleCharAppendKey('s'))
            .append(new SimpleCharAppendKey('d'))
            .append(new SimpleCharAppendKey('f'))
            .append(new SimpleCharAppendKey('g'))
            .append(new SimpleCharAppendKey('h'))
            .append(new SimpleCharAppendKey('j'))
            .append(new SimpleCharAppendKey('k'))
            .append(new SimpleCharAppendKey('l'))
            .buildRow()
            .row()
            .append(new SimpleCharAppendKey('z'))
            .append(new SimpleCharAppendKey('x'))
            .append(new SimpleCharAppendKey('c'))
            .append(new SimpleCharAppendKey('v'))
            .append(new SimpleCharAppendKey('b'))
            .append(new SimpleCharAppendKey('n'))
            .append(new SimpleCharAppendKey('m'))
            .append(new SimpleCharAppendBackspaceKey())
            .buildRow()
            .row()
            .append(new SimpleCharAppendKey(')'))
            .append(new SimpleCharAppendSpaceKey())
            .append(new SimpleCharAppendKey('.'))
            .append(new SimpleCharAppendEnterKey())
            .buildRow()
            .build();
    }

    public KeyboardIMEView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeyboardIMEView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public KeyboardIMEView service(KeyboardIMEService service) {
        this.service = service;
        return this;
    }

    private KeyboardKey unprojectToKey(int x, int y) {
        // Determine row from y and ensure valid
        var rows = locale.rowCount();
        var row = floorDiv(y, KEY_HEIGHT);
        if (!(row >= 0 && row < rows)) return null;

        // Determine col from x and ensure valid
        var cols = locale.colCount(row);
        var totalScreenWidth = getWidth();
        var totalKeysWidth = cols * KEY_WIDTH_REGULAR;
        var min = floorDiv(totalScreenWidth - totalKeysWidth, 2);
        var max = floorDiv(totalScreenWidth + totalKeysWidth, 2);
        var col = floorDiv(KEY_WIDTH_REGULAR * (x - min), max - min);
        if (!(col >= 0 && col <= max)) return null;

        return locale.key(col, row);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        var rows = locale.rowCount();
        var totalKeysHeight = rows * KEY_HEIGHT;
        setMeasuredDimension(
            getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
            totalKeysHeight);
    }

    protected void onDraw(Canvas canvas) {
        var currentComposingText = service.currentComposingText();
        var fontVerticalShift
            = KEY_TEXT.getFontMetrics().top
            - KEY_TEXT.getFontMetrics().ascent;

        var totalScreenWidth = getWidth();
        var totalScreenHeight = getHeight();

        var rows = locale.rowCount();
        for (var row = 0; row < rows; row++) {
            var y = row * KEY_HEIGHT;

            var cols = locale.colCount(row);
            var totalKeysWidth = cols * KEY_WIDTH_REGULAR;
            var min = floorDiv(totalScreenWidth - totalKeysWidth, 2);
            for (var col = 0; col < cols; col++) {
                var x = min + KEY_WIDTH_REGULAR * col;

                var key = locale.key(col, row);
                assert key != null;

                canvas.drawRoundRect(
                    x, y,
                    x + KEY_WIDTH_REGULAR,
                    y + KEY_HEIGHT,
                    KEY_CORNER_RADIUS,
                    KEY_CORNER_RADIUS,
                    currentlyDown == key
                        ? KEY_BACKGROUND_DOWN
                        : KEY_BACKGROUND);
                canvas.drawText(
                    key.display(currentComposingText),
                    x + KEY_WIDTH_REGULAR / 2f,
                    y + KEY_HEIGHT / 2f - fontVerticalShift,
                    KEY_TEXT);
            }
        }
    }

    private KeyboardKey currentlyDown;

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

                break;
            }
            case MotionEvent.ACTION_UP: {
                if (currentlyDown != key || key == null) break;

                var previousComposingText = service.currentComposingText();
                var composingTextResult = currentlyDown.transformText(previousComposingText);
                service.updateComposingText(composingTextResult);
                currentlyDown = null;

                break;
            }
        }

        return true;
    }
}