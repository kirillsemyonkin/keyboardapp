package kirillsemyonkin.keyboardapp.icon;

import static kirillsemyonkin.keyboardapp.view.KeyboardAppView.KEY_TEXT;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Canvas;

import kirillsemyonkin.keyboardapp.R;

public enum PredefinedKeyIcon implements KeyIcon {
    SPACE {
        public void draw(Canvas canvas, Resources resources) {
            // can do a ␣
        }

        public char character() {
            return ' ';
        }
    },
    SHIFT {
        public void draw(Canvas canvas, Resources resources) {
            @SuppressLint("UseCompatLoadingForDrawables")
            var icon = resources
                .getDrawable(
                    R.drawable.ic_shift,
                    resources.newTheme());
            var size = (int) KEY_TEXT.getTextSize();
            icon.setBounds(-size, -size, size, size);
            icon.draw(canvas);
        }
    },
    BACKSPACE {
        public void draw(Canvas canvas, Resources resources) {
            @SuppressLint("UseCompatLoadingForDrawables")
            var icon = resources
                .getDrawable(
                    R.drawable.ic_backspace,
                    resources.newTheme());
            var size = (int) KEY_TEXT.getTextSize();
            icon.setBounds(-size, -size, size, size);
            icon.draw(canvas);
        }
    },
    ENTER {
        public void draw(Canvas canvas, Resources resources) {
            @SuppressLint("UseCompatLoadingForDrawables")
            var icon = resources
                .getDrawable(
                    R.drawable.ic_enter,
                    resources.newTheme());
            var size = (int) (KEY_TEXT.getTextSize() / 1.2f);
            icon.setBounds(-size, -size, size, size);
            icon.draw(canvas);
        }
    },
    LANGUAGE {
        public void draw(Canvas canvas, Resources resources) {
            // TODO
        }
    },
    SETTINGS {
        public void draw(Canvas canvas, Resources resources) {
            // TODO
        }
    };

    public char character() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("<icon> requires a proper action to follow it");
    }
}
