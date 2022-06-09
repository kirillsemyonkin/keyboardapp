package kirillsemyonkin.keyboardapp.icon;

import android.graphics.Canvas;

public enum PredefinedKeyIcon implements KeyIcon {
    SPACE {
        public void draw(Canvas canvas) {
            // TODO
        }
    },
    SHIFT {
        public void draw(Canvas canvas) {
            // TODO
        }
    },
    BACKSPACE {
        public void draw(Canvas canvas) {
            // TODO
        }
    },
    ENTER {
        public void draw(Canvas canvas) {
            // TODO
        }
    };

    public char character() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("<icon> requires a proper action to follow it");
    }
}
