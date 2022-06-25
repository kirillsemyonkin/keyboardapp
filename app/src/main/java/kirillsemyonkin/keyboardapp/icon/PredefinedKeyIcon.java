package kirillsemyonkin.keyboardapp.icon;

import static kirillsemyonkin.keyboardapp.icon.PlainTextKeyIcon.drawText;
import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_TEXT;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Canvas;

import kirillsemyonkin.keyboardapp.R;

public enum PredefinedKeyIcon implements KeyIcon {
    SPACE {
        public void draw(Canvas canvas, Resources resources) {
        }

        public char character() {
            return ' ';
        }
    },
    SPACE_ALT {
        public void draw(Canvas canvas, Resources resources) {
            drawText(canvas, "␣");
        }

        public char character() {
            return ' ';
        }
    },
    SHIFT {
        public void draw(Canvas canvas, Resources resources) {
            drawIcon(canvas, resources, R.drawable.ic_shift, 1);
        }
    },
    BACKSPACE {
        public void draw(Canvas canvas, Resources resources) {
            drawIcon(canvas, resources, R.drawable.ic_backspace, 1);
        }
    },
    ENTER {
        public void draw(Canvas canvas, Resources resources) {
            drawIcon(canvas, resources, R.drawable.ic_enter, 0.8f);
        }
    },
    LANGUAGE {
        public void draw(Canvas canvas, Resources resources) {
            drawIcon(canvas, resources, R.drawable.ic_language, 1.1f);
        }
    },
    SETTINGS {
        public void draw(Canvas canvas, Resources resources) {
            drawIcon(canvas, resources, R.drawable.ic_settings, 0.8f);
        }
    },
    HIDE {
        public void draw(Canvas canvas, Resources resources) {
            drawIcon(canvas, resources, R.drawable.ic_hide, 1f);
        }
    };

    public char character() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("<icon> requires a proper action to follow it");
    }

    public static void drawIcon(Canvas canvas, Resources resources, int icon, float scale) {
        @SuppressLint("UseCompatLoadingForDrawables")
        var drawable = resources
            .getDrawable(
                icon,
                resources.newTheme());
        var size = (int) (KEY_TEXT.getTextSize() * scale);
        drawable.setBounds(-size, -size, size, size);
        drawable.draw(canvas);
    }
}
