package kirillsemyonkin.keyboardapp.action;

import static org.xmlpull.v1.XmlPullParser.TEXT;
import static kirillsemyonkin.keyboardapp.layout.KeyboardLocale.XMLNS_NULL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import kirillsemyonkin.keyboardapp.icon.KeyIcon;

public enum Keys {
    BACKSPACE {
        public KeyboardKey parse(XmlPullParser parser, KeyIcon icon, float growthFactor, boolean highlight) {
            return new BackspaceKey(icon, growthFactor, highlight);
        }
    },
    ALT {
        public KeyboardKey parse(XmlPullParser parser, KeyIcon icon, float growthFactor, boolean highlight)
            throws XmlPullParserException,
            IOException {
            parser.next();
            parser.require(TEXT, XMLNS_NULL, null);
            var altChars = parser.getText().trim();

            return new AltCharAppendKey(icon, growthFactor, highlight, altChars.toCharArray());
        }
    },
    SWITCH {
        public KeyboardKey parse(XmlPullParser parser, KeyIcon icon, float growthFactor, boolean highlight)
            throws XmlPullParserException,
            IOException {
            parser.next();
            parser.require(TEXT, XMLNS_NULL, null);
            var mode = parser.getText().trim();

            return new SwitchModeKey(icon, growthFactor, highlight, mode);
        }
    },
    ENTER {
        public KeyboardKey parse(XmlPullParser parser, KeyIcon icon, float growthFactor, boolean highlight) {
            return new EnterKey(icon, growthFactor, highlight);
        }
    };

    public abstract KeyboardKey parse(XmlPullParser parser, KeyIcon icon, float growthFactor, boolean highlight)
        throws XmlPullParserException,
        IOException;
}
