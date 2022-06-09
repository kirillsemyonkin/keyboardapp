package kirillsemyonkin.keyboardapp.action;

import static org.xmlpull.v1.XmlPullParser.TEXT;
import static kirillsemyonkin.keyboardapp.KeyboardLocale.XMLNS_NULL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import kirillsemyonkin.keyboardapp.icon.KeyIcon;

public enum Keys {
    BACKSPACE {
        public KeyboardKey parse(XmlPullParser parser, KeyIcon icon, float growthFactor) {
            return new BackspaceKey(icon, growthFactor);
        }
    },
    ALT {
        public KeyboardKey parse(XmlPullParser parser, KeyIcon icon, float growthFactor)
            throws XmlPullParserException,
            IOException {
            parser.next();
            parser.require(TEXT, XMLNS_NULL, null);
            var altChars = parser.getText().trim();

            return new AltCharAppendKey(icon, growthFactor, altChars.toCharArray());
        }
    },
    SWITCH {
        public KeyboardKey parse(XmlPullParser parser, KeyIcon icon, float growthFactor)
            throws XmlPullParserException,
            IOException {
            parser.next();
            parser.require(TEXT, XMLNS_NULL, null);
            var mode = parser.getText().trim();

            return new SwitchModeKey(icon, growthFactor, mode);
        }
    },
    COMMIT {
        public KeyboardKey parse(XmlPullParser parser, KeyIcon icon, float growthFactor)
            throws XmlPullParserException,
            IOException {
            parser.next();
            parser.require(TEXT, XMLNS_NULL, null);
            var text = parser.getText().trim();
            if ("\\n".equals(text)) text = "\n";
            if ("\\s".equals(text)) text = " ";

            return new CommitKey(icon, growthFactor, text);
        }
    };

    public abstract KeyboardKey parse(XmlPullParser parser, KeyIcon icon, float growthFactor)
        throws XmlPullParserException,
        IOException;
}
