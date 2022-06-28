package kirillsemyonkin.keyboardapp.layout;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;
import static java.lang.Float.parseFloat;
import static java.lang.Math.max;
import static java.util.Collections.unmodifiableMap;
import static java.util.Map.entry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import kirillsemyonkin.keyboardapp.action.AltCharAppendKey;
import kirillsemyonkin.keyboardapp.action.KeyboardKey;
import kirillsemyonkin.keyboardapp.action.Keys;
import kirillsemyonkin.keyboardapp.action.SwitchModeKey;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;
import kirillsemyonkin.keyboardapp.icon.PlainTextKeyIcon;
import kirillsemyonkin.keyboardapp.icon.PredefinedKeyIcon;
import kirillsemyonkin.keyboardapp.util.Highlight;

public final class KeyboardLocale {
    private final String id;
    private final Map<String, KeyboardLayout> layouts;
    private final String defaultMode;

    public static final String XMLNS_NULL = null;

    private KeyboardLocale(String id, Map<String, KeyboardLayout> layouts, String defaultMode) {
        this.id = id;
        this.layouts = unmodifiableMap(layouts);
        this.defaultMode = defaultMode;
    }

    public String id() {
        return id;
    }

    public KeyboardLayout layout(String mode) {
        return layouts.get(mode);
    }

    public String defaultMode() {
        return defaultMode;
    }

    //
    // Parse
    //

    public static KeyboardLocale parseLocale(String locale, XmlPullParser parser)
        throws XmlPullParserException,
        IOException,
        NumberFormatException,
        NullPointerException,
        IllegalArgumentException {
        // Read <keyboardLocale defaultMode="string">
        parser.require(START_TAG, XMLNS_NULL, "keyboardLocale");

        var defaultMode = parser.getAttributeValue(null, "defaultMode");
        if (defaultMode == null)
            throw new NullPointerException("<keyboardLocale> missing `defaultMode` attribute");

        // Parse <layout>s until </keyboardLocale>
        var layouts = new HashMap<String, KeyboardLayout>();
        while (parser.next() != END_TAG) {
            var layoutEntry = parseLayout(parser);
            layouts.put(
                layoutEntry.getKey(),
                layoutEntry.getValue());
        }

        // Read </keyboardLocale>
        parser.require(END_TAG, XMLNS_NULL, "keyboardLocale");

        // Ensure defaultMode is valid
        if (!layouts.containsKey(defaultMode))
            throw new NullPointerException("<keyboardLocale> `defaultMode` attribute does not correspond to any of the <layout>s");

        // Ensure all <switch>es are valid
        for (var layout : layouts.values())
            for (int rowNum = 0, rows = layout.rowCount(); rowNum < rows; rowNum++) {
                var row = layout.row(rowNum);
                assert row != null;

                for (int colNum = 0, cols = row.colCount(); colNum < cols; colNum++) {
                    var key = row.key(colNum);
                    assert key != null;

                    if (key instanceof SwitchModeKey) {
                        var mode = ((SwitchModeKey) key).mode();
                        if (!layouts.containsKey(mode))
                            throw new NullPointerException("<switch>" + mode + "</switch> does not correspond to any of the <layout>s");
                    }
                }
            }

        return new KeyboardLocale(locale, layouts, defaultMode);
    }

    private static Entry<String, KeyboardLayout> parseLayout(XmlPullParser parser)
        throws XmlPullParserException,
        IOException,
        NumberFormatException,
        NullPointerException,
        IllegalArgumentException {
        // Read <layout mode="string" [growthFactor="float"]>
        parser.require(START_TAG, XMLNS_NULL, "layout");

        var mode = parser.getAttributeValue(XMLNS_NULL, "mode");
        if (mode == null)
            throw new NullPointerException("<layout> missing `mode` attribute");

        var growthFactorOpt = parser.getAttributeValue(XMLNS_NULL, "growthFactor");
        var growthFactor
            = growthFactorOpt == null
            ? 0
            : parseFloat(growthFactorOpt); // early parse

        // Parse <row>s until </layout>
        var rows = new ArrayList<KeyboardRow>();
        while (parser.next() != END_TAG)
            rows.add(parseRow(parser));

        // Read </layout>
        parser.require(END_TAG, XMLNS_NULL, "layout");

        // If growthFactor is unspecified (or non-positive), detect it from max row length
        if (growthFactor <= 0)
            for (var row : rows) {
                var factor = row.growthFactor();
                if (factor <= 0) {
                    factor = 0;
                    for (int colNum = 0, cols = row.colCount(); colNum < cols; colNum++)
                        factor += row.key(colNum).growthFactor();
                }
                growthFactor = max(growthFactor, factor);
            }

        return entry(mode, new KeyboardLayout(rows, growthFactor));
    }

    private static KeyboardRow parseRow(XmlPullParser parser)
        throws XmlPullParserException,
        IOException,
        NumberFormatException,
        IllegalArgumentException {
        // Read <row [growthFactor="float"]>
        parser.require(START_TAG, XMLNS_NULL, "row");

        var growthFactorOpt = parser.getAttributeValue(XMLNS_NULL, "growthFactor");
        var growthFactor
            = growthFactorOpt == null
            ? 0
            : parseFloat(growthFactorOpt); // early parse

        // Parse <key>s until </row>
        var keys = new ArrayList<KeyboardKey>();
        while (parser.next() != END_TAG)
            keys.add(parseKey(parser));

        // Read </row>
        parser.require(END_TAG, XMLNS_NULL, "row");

        return new KeyboardRow(keys, growthFactor);
    }

    private static KeyboardKey parseKey(XmlPullParser parser)
        throws XmlPullParserException,
        IOException,
        NumberFormatException,
        IllegalArgumentException {
        // Read <key [growthFactor="float"] [highlight="bool"]>
        parser.require(START_TAG, XMLNS_NULL, "key");

        var growthFactorOpt = parser.getAttributeValue(XMLNS_NULL, "growthFactor");
        var growthFactor
            = growthFactorOpt == null
            ? 1
            : max(0, parseFloat(growthFactorOpt)); // early parse

        var highlightOpt = parser.getAttributeValue(XMLNS_NULL, "highlight");
        var highlight = highlightOpt == null
            ? Highlight.FALSE
            : Highlight.valueOf(highlightOpt.toUpperCase());

        var icon = parseIcon(parser);
        return parseAction(parser, icon, growthFactor, highlight);
    }

    private static KeyIcon parseIcon(XmlPullParser parser)
        throws XmlPullParserException,
        IOException,
        IllegalArgumentException {
        // <key>char ...</key>
        if (parser.next() == TEXT)
            return new PlainTextKeyIcon(parser.getText().trim());

        // <key><icon>...</icon> ...</key>
        parser.require(START_TAG, XMLNS_NULL, "icon");
        parser.next();
        parser.require(TEXT, XMLNS_NULL, null);

        var icon = PredefinedKeyIcon
            .valueOf(parser.getText().trim().toUpperCase());

        parser.next();
        parser.require(END_TAG, XMLNS_NULL, "icon");

        return icon;
    }

    private static KeyboardKey parseAction(XmlPullParser parser,
                                           KeyIcon icon, float growthFactor, Highlight highlight)
        throws XmlPullParserException,
        IOException {
        // char</key>
        if (parser.next() == END_TAG) {
            parser.require(END_TAG, XMLNS_NULL, "key");
            return new AltCharAppendKey(icon, growthFactor, highlight);
        }

        // <key>icon action</key>
        // Read a tag with any name
        parser.require(START_TAG, XMLNS_NULL, null);
        var action = parser.getName();

        var key = Keys
            .valueOf(action.toUpperCase())
            .parse(parser, icon, growthFactor, highlight);

        // Read </action>
        parser.next();
        parser.require(END_TAG, XMLNS_NULL, action);

        // Read </key>
        parser.next();
        parser.require(END_TAG, XMLNS_NULL, "key");

        return key;
    }
}
